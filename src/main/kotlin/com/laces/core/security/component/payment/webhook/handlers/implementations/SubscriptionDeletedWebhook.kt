package com.laces.core.security.component.payment.webhook.handlers.implementations

import com.laces.core.security.component.payment.webhook.handlers.WebhookEvent
import com.laces.core.security.component.payment.webhook.handlers.WebhookProcessor
import com.laces.core.security.component.user.SubscriptionState
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.stripe.model.Event
import com.stripe.model.Subscription
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@WebhookEvent("customer.subscription.deleted")
class SubscriptionDeletedWebhook(
        val userService: UserService
): WebhookProcessor {

    companion object {
        private val LOG = LoggerFactory.getLogger(SubscriptionDeletedWebhook::class.java)
    }

    override fun process(event: Event) {
        val subscription: Subscription = event.data.`object` as Subscription
        val user = cancelUserSubscription(subscription)
        LOG.info("Cancelling subscription: ${user.id}")
        userService.expireUserSessions(user)
    }

    private fun cancelUserSubscription(subscription: Subscription): User {

        val user = userService.getUserBySubscription(subscription.id)
        user.subscriptionState = SubscriptionState.CANCELLED
        userService.save(user)
        return user
    }
}