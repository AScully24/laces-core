package com.laces.core.security.component.payment

import com.laces.core.responses.UserCustomerStripeIdException
import com.laces.core.responses.UserSubscriptionStripeIdException
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.SubscriptionState
import com.laces.core.security.component.user.subscription.SubscriptionState.ACTIVE
import com.laces.core.security.component.user.subscription.SubscriptionState.CANCEL_PENDING
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.Subscription
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional


@Service
@ConditionalOnProperty("app.stripe.enabled")
class PaymentService(
        @Value("\${app.stripe.secret}")
        secret: String,
        val userService: UserService,
        val planService: SubscriptionPlanService,
        val stripeSubscriptionValidator: StripeSubscriptionValidator
) {
    init {
        Stripe.apiKey = secret
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentService::class.java)
    }

    @Transactional
    fun cancelUserSubscription(user: User) {

        if (StringUtils.isBlank(user.subscriptionStripeId)) {
            throw UserSubscriptionStripeIdException("User is not registered to a subscription")
        }

        val subscription = Subscription.retrieve(user.subscriptionStripeId)
        val params = HashMap<String, Any>()
        params["at_period_end"] = true
        subscription.cancel(params)

        val updatedUser = user.copy(subscriptionState = CANCEL_PENDING)
        userService.save(updatedUser)
    }

    @Transactional
    fun changeUserSubscription(user: User, newPlanStripeId: String) {

        if (StringUtils.isBlank(user.subscriptionStripeId)) {
            throw UserSubscriptionStripeIdException("User is not registered to a subscription")
        }

        val newMeteredStripeId = planService.findSubscriptionPlan(newPlanStripeId)?.meteredStripeId

        val subscription = Subscription.retrieve(user.subscriptionStripeId)
        if (subscription == null) {
            LOGGER.info("Unable to change subscription for user ${user.id}. Does not exist in Stripe")
            throw RuntimeException("You must reactivate your subscription before changing it.")
        }
        updateStripeSubscription(subscription, newPlanStripeId, newMeteredStripeId)

        userService.save(user.copy(
                planStripeId = newPlanStripeId,
                meteredStripeId = newMeteredStripeId
        ))

    }

    @Transactional
    fun reactivateUserSubscriptionPendingCancellation(user: User) {
        val subscription = Subscription.retrieve(user.subscriptionStripeId)
        stripeSubscriptionValidator.checkCancelPending(subscription)
        reactivateCancelPending(subscription)
    }

    @Transactional
    fun reactivateCancelledUserSubscription(user: User, planStripeId: String, token: String) {
        val subscription = Subscription.retrieve(user.subscriptionStripeId)

        stripeSubscriptionValidator.checkCancelled(subscription)

        val customer = Customer.retrieve(user.customerStripeId)
        updateCustomerPaymentDetails(customer, token)
        signUpCustomerToSubscriptionAndUpdateSubscriptionDetails(planStripeId, customer, user, ACTIVE)
    }

    fun updateUserPaymentDetails(user: User, token: String) {
        val customer = Customer.retrieve(user.customerStripeId)
        updateCustomerPaymentDetails(customer, token)
    }

    private fun updateCustomerPaymentDetails(customer: Customer, token: String) {
        val chargeParams = HashMap<String, Any>()
        chargeParams["source"] = token
        customer.update(chargeParams)
    }

    private fun reactivateCancelPending(subscription: Subscription) {
        val newSubscriptionItems = HashMap<String, Any>()
        val previousSubscriptionItems = subscription.subscriptionItems.data

        val mainPlan = HashMap<String, Any>()
        mainPlan["id"] = previousSubscriptionItems[0].id
        mainPlan["plan"] = previousSubscriptionItems[0].plan.id
        newSubscriptionItems["0"] = mainPlan

        if (previousSubscriptionItems[1] != null) {
            val meteredPlan = HashMap<String, Any>()
            meteredPlan["id"] = previousSubscriptionItems[1].id
            meteredPlan["plan"] = previousSubscriptionItems[1].plan.id
            newSubscriptionItems["1"] = meteredPlan
        }

        val params = HashMap<String, Any>()
        params["cancel_at_period_end"] = false
        params["items"] = newSubscriptionItems

        subscription.update(params)
    }

    @Transactional
    fun createCustomerAndSignUpToSubscription(user: User, token: String, planStripeId: String, subscriptionState: SubscriptionState = ACTIVE): Subscription {
        val (customer, updatedUser) = createCustomer(user, token)
        return signUpCustomerToSubscriptionAndUpdateSubscriptionDetails(planStripeId, customer, updatedUser, subscriptionState)
    }

    private fun signUpCustomerToSubscriptionAndUpdateSubscriptionDetails(
            planStripeId: String,
            customer: Customer,
            user: User,
            subscriptionState: SubscriptionState
    ): Subscription {
        val meteredStripeId = planService.findSubscriptionPlan(planStripeId)?.meteredStripeId
        val subscription = signUpCustomerToSubscription(customer, planStripeId, meteredStripeId)
        updateUserSubscriptionDetails(user, subscription, planStripeId, meteredStripeId, subscriptionState)
        return subscription
    }

    private fun updateUserSubscriptionDetails(user: User, subscription: Subscription, planStripeId: String, meteredStripeId: String?, subscriptionState: SubscriptionState) {

        // There should only be a single plan per subscriber, but this allows multiple plans.
        // Need to ensure more than one of the same plan is on any single subscription.
        val planId = subscription.subscriptionItems.data.first { it.plan.id == planStripeId }.id

        val updatedUser = user.copy(subscriptionStripeId = subscription.id,
                subscriptionState = subscriptionState,
                planStripeId = planStripeId,
                meteredStripeId = meteredStripeId,
                subscriptionItemId = planId
        )

        userService.save(updatedUser)
    }

    fun getCurrentUserStripeSubscription(): Subscription {
        return getUserStripeSubscription(userService.getCurrentUser())
    }

    fun getUserStripeSubscription(user: User): Subscription {

        if (StringUtils.isBlank(user.subscriptionStripeId)) {
            throw UserSubscriptionStripeIdException("User is not registered to a subscription")
        }
        return Subscription.retrieve(user.subscriptionStripeId)
    }

    @Transactional
    private fun createCustomer(user: User, token: String): Pair<Customer, User> {

        if (!StringUtils.isBlank(user.customerStripeId)) {
            throw UserCustomerStripeIdException("User is already registered with a payment.")
        }

        val chargeParams = HashMap<String, Any>()
        chargeParams["source"] = token
        chargeParams["email"] = user.username

        val customer = Customer.create(chargeParams)
        val updatedUser = user.copy(customerStripeId = customer.id)
        val savedUser = userService.save(updatedUser)

        return Pair(customer, savedUser)

    }

    private fun signUpCustomerToSubscription(customer: Customer, productStripeId: String, meteredStripeId: String?): Subscription {

        val subscription = HashMap<String, Any>()

        val mainPlan = HashMap<String, Any>()
        mainPlan["plan"] = productStripeId
        subscription["0"] = mainPlan

        if (meteredStripeId != null) {
            val meteredPlan = HashMap<String, Any>()
            meteredPlan["plan"] = meteredStripeId
            subscription["1"] = meteredPlan
        }

        val subscriptionRequest = HashMap<String, Any>()
        subscriptionRequest["customer"] = customer.id
        subscriptionRequest["items"] = subscription

        try {
            return Subscription.create(subscriptionRequest)
        } catch (e: Exception) {
            LOGGER.error("Failed to sign user up to subscription", e)
            throw UserSubscriptionStripeIdException("Failed to sign user up to subscription: $productStripeId")
        }
    }

    private fun updateStripeSubscription(subscription: Subscription, newPlanStripeId: String, newMeteredStripeId: String?) {
        val plans = HashMap<String, Any>()

        val mainPlan = HashMap<String, Any>()
        mainPlan["id"] = subscription.subscriptionItems.data[0].id
        mainPlan["plan"] = newPlanStripeId
        plans["0"] = mainPlan

        if (newMeteredStripeId != null) {
            val meteredPlan = HashMap<String, Any>()
            meteredPlan["id"] = subscription.subscriptionItems.data[1].id
            meteredPlan["plan"] = newMeteredStripeId
            plans["1"] = meteredPlan
        }

        val params = HashMap<String, Any>()
        params["cancel_at_period_end"] = false
        params["items"] = plans
        subscription.update(params)
    }

}