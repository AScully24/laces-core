package com.laces.core.security.component.payment

import com.laces.core.responses.UserCustomerStripeIdException
import com.laces.core.responses.UserDoesNotHavePaymentMethod
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
import com.stripe.param.SubscriptionCreateParams
import com.stripe.param.SubscriptionUpdateParams
import com.stripe.param.SubscriptionUpdateParams.ProrationBehavior.CREATE_PRORATIONS
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

        val params = SubscriptionUpdateParams.builder()
            .setCancelAtPeriodEnd(true)
            .build()

        subscription.update(params)

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

        val previousSubscriptionItems = subscription.items.data

        val params = SubscriptionUpdateParams.builder()
            .setCancelAtPeriodEnd(false)
            .setProrationBehavior(CREATE_PRORATIONS)
            .addItem(
                SubscriptionUpdateParams.Item.builder()
                    .setId(previousSubscriptionItems[0].id)
                    .setPrice(previousSubscriptionItems[0].plan.id)
                    .build()
            )


        if (previousSubscriptionItems[1] != null) {
            params.addItem(
                SubscriptionUpdateParams.Item.builder()
                    .setId(previousSubscriptionItems[1].id)
                    .setPrice(previousSubscriptionItems[1].plan.id)
                    .build()
            )
        }

        subscription.update(params.build())
    }

    @Transactional
    fun createCustomerAndSignUpToSubscription(user: User, token: String?, planStripeId: String, subscriptionState: SubscriptionState = ACTIVE): Subscription {
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
        val planId = subscription.items.data.first { it.plan.id == planStripeId }.id

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
    private fun createCustomer(user: User, token: String?): Pair<Customer, User> {

        if (!StringUtils.isBlank(user.customerStripeId)) {
            throw UserCustomerStripeIdException("User is already registered with a payment.")
        }

        val chargeParams = HashMap<String, Any>()
        token?.let { chargeParams["source"] = it }
        chargeParams["email"] = user.username

        val customer = Customer.create(chargeParams)
        val updatedUser = user.copy(customerStripeId = customer.id)
        val savedUser = userService.save(updatedUser)

        return Pair(customer, savedUser)

    }

    private fun signUpCustomerToSubscription(customer: Customer, productStripeId: String, meteredStripeId: String?): Subscription {

        val subscriptionPlan = planService.findSubscriptionPlan(productStripeId)
        if(subscriptionPlan?.free == false && customer.defaultSource == null){
            throw UserDoesNotHavePaymentMethod()
        }

        val paramBuilder = SubscriptionCreateParams.builder()
            .setCustomer(customer.id)
            .addItem(
                SubscriptionCreateParams.Item.builder()
                    .setPrice(productStripeId)
                    .build()
            )

        if (meteredStripeId != null) {
            paramBuilder.addItem(
                SubscriptionCreateParams.Item.builder()
                    .setPrice(meteredStripeId)
                    .build()
            )
        }

        try {
            return Subscription.create(paramBuilder.build())
        } catch (e: Exception) {
            LOGGER.error("Failed to sign user up to subscription", e)
            throw UserSubscriptionStripeIdException("Failed to sign user up to subscription: $productStripeId")
        }
    }

    private fun updateStripeSubscription(subscription: Subscription, newPlanStripeId: String, newMeteredStripeId: String?) {

        val paramsBuilder = SubscriptionUpdateParams.builder()
            .setCancelAtPeriodEnd(false)
            .setProrationBehavior(CREATE_PRORATIONS)
            .addItem(
                SubscriptionUpdateParams.Item.builder()
                    .setId(subscription.items.data[0].id)
                    .setPrice(newPlanStripeId)
                    .build()
            )

        if (newMeteredStripeId != null) {
            paramsBuilder.addItem(
                SubscriptionUpdateParams.Item.builder()
                    .setId(subscription.items.data[1].id)
                    .setPrice(newMeteredStripeId)
                    .build()
            )
        }

        subscription.update(paramsBuilder.build())
    }

}