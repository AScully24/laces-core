package com.laces.core.security.component.payment

import com.laces.core.security.component.user.MyUserPrincipal
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.stripe.Stripe
import com.stripe.model.Subscription
import com.stripe.net.Webhook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.session.SessionRegistry
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional


@Service
class StripeWebhookService {

    @Value("\${app.stripe.secret}")
    lateinit var secret: String

    @Value("\${app.stripe.webhook.signing-secret}")
    lateinit var signingSecret: String

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var sessionRegistry: SessionRegistry


    @PostConstruct
    fun init(){
        Stripe.apiKey = secret
    }

    @Transactional
    fun processJsonEventString(stripeSignature :String, jsonEvent: String){
        val event = Webhook.constructEvent(jsonEvent,stripeSignature, signingSecret)

        if (event.type == "customer.subscription.deleted") {
            val subscription: Subscription = event.data.`object` as Subscription
            val user = cancelUserSubscription(subscription)
            expireUserSessions(user.username)
        }
    }

    private fun cancelUserSubscription(subscription: Subscription): User {
        val user = userService.getUserBySubscription(subscription.id)

        user.subscriptionActive = false
        user.subscriptionCancelPending = false
        userService.save(user)
        return user
    }

    private fun expireUserSessions(username: String) {
        sessionRegistry.allPrincipals
                .filterIsInstance(MyUserPrincipal::class.java)
                .filter { it.username == username }
                .flatMap { sessionRegistry.getAllSessions(it, true) }
                .forEach { it.expireNow() }

    }
}