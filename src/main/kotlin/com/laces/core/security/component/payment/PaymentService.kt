package com.laces.core.security.component.payment

import com.laces.core.responses.UserCustomerStripeIdException
import com.laces.core.responses.UserSubscriptionStripeIdException
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.Subscription
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@ConditionalOnProperty("app.stripe.enabled")
class PaymentService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentService::class.java)
    }

    @Value("\${app.stripe.api-key}")
    lateinit var apiKey : String

    @Value("\${app.stripe.secret}")
    lateinit var secret: String

    @Autowired
    lateinit var userService : UserService

    @PostConstruct
    fun init(){
        Stripe.apiKey = secret
    }

    @Transactional
    fun cancelUserSubscription(user:User){

        if (StringUtils.isBlank(user.subscriptionStripeId)) {
            throw UserSubscriptionStripeIdException("User is not registered to a subscription")
        }

        val subscription = Subscription.retrieve(user.subscriptionStripeId)
        val params = HashMap<String, Any>()
        params["at_period_end"] = true
        subscription.cancel(params)

        user.subscriptionCancelPending = true
        
        userService.save(user)
    }

    @Transactional
    fun changeUserSubscription(user:User, newPlanStripeId: String){

        if (StringUtils.isBlank(user.subscriptionStripeId)) {
            throw UserSubscriptionStripeIdException("User is not registered to a subscription")
        }

        val subscription = Subscription.retrieve(user.subscriptionStripeId)

        val item = HashMap<String,Any>()
        item["id"] = subscription.subscriptionItems.data[0].id
        item["plan"] = newPlanStripeId

        val items = HashMap<String,Any>()
        items["0"] = item

        val params = HashMap<String,Any>()
        params["cancel_at_period_end"] = false
        params["items"] = items

        subscription.update(params)
        user.planStripeId = newPlanStripeId

        userService.save(user)

    }

    @Transactional
    fun createCustomerAndSignUpToSubscription(user: User, token: String, planStripeId: String): Subscription {
        val customer = createCustomer(user, token)

        val subscription = signUpCustomerToSubscription(customer, planStripeId)

        user.subscriptionStripeId = subscription.id

        user.subscriptionActive = true
        user.planStripeId = planStripeId

        userService.save(user)

        return subscription
    }

    private fun signUpCustomerToSubscription(customer: Customer, productStripeId: String): Subscription {
        val item = HashMap<String,Any>()
        item["plan"] = productStripeId
        val items = HashMap<String,Any>()
        items["0"] = item
        val params = HashMap<String,Any>()
        params["customer"] = customer.id
        params["items"] = items

        try {
            return Subscription.create(params)
        } catch (e: Exception) {
            LOGGER.error("Failed to sign user up to subscription", e)
            throw UserSubscriptionStripeIdException("Failed to sign user up to subscription: $productStripeId")
        }
    }

    @Transactional
    private fun createCustomer(user: User, token : String): Customer {

        if (!StringUtils.isBlank(user.customerStripeId)){
            throw UserCustomerStripeIdException("User is already registered with a payment.")
        }

        val chargeParams = HashMap<String, Any>()
        chargeParams["source"] = token
        chargeParams["email"] = user.username

        val customer = Customer.create(chargeParams)
        user.customerStripeId = customer.id
        userService.save(user)

        return customer

    }
}