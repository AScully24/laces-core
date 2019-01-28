package com.laces.core.security.component.payment.webhook.handlers.implementations

import com.laces.core.security.component.payment.webhook.handlers.ACTIVE
import com.laces.core.security.component.payment.webhook.handlers.UNPAID
import com.laces.core.security.component.payment.webhook.handlers.WebhookEvent
import com.laces.core.security.component.payment.webhook.handlers.WebhookProcessor
import com.laces.core.security.component.user.SubscriptionState
import com.laces.core.security.component.user.SubscriptionState.*
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.stripe.model.Event
import com.stripe.model.Subscription
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@WebhookEvent("customer.subscription.updated")
class SubscriptionUpdatedWebhook(
        val userService: UserService
) : WebhookProcessor {

    companion object {
        private val LOG = LoggerFactory.getLogger(SubscriptionUpdatedWebhook::class.java)
    }

    override fun process(event: Event) {
        val subscription: Subscription = event.data.`object` as Subscription
        val user = userService.getUserBySubscription(subscription.id)
        when {
            isSetToCancelledPending(subscription, user) -> cancelPendingUserSubscription(user)
            subscription.status == UNPAID -> setUserSubscriptionToUnpaid(user)
            isSetToActive(subscription, user) -> setUserSubscriptionToActive(user)
        }
    }

    // Only update to active if the user is not active
    private fun isSetToActive(subscription: Subscription, user: User) =
            subscription.status == ACTIVE &&
                    !subscription.cancelAtPeriodEnd &&
                    user.subscriptionState != SubscriptionState.ACTIVE

    private fun cancelPendingUserSubscription(user: User) {
        LOG.info("Cancelling user subscription: ${user.id}")
        user.subscriptionState = CANCEL_PENDING
        userService.save(user)
    }

    private fun isSetToCancelledPending(subscription: Subscription, user: User): Boolean {
        return subscription.cancelAtPeriodEnd &&
                (user.subscriptionState != CANCEL_PENDING || user.subscriptionState != CANCELLED)
    }

    private fun setUserSubscriptionToUnpaid(user: User) {
        LOG.info("User subscription not paid: ${user.id}")
        user.subscriptionState = SubscriptionState.UNPAID
        userService.save(user)
        userService.expireUserSessions(user)
    }

    private fun setUserSubscriptionToActive(user: User) {
        LOG.info("User subscription activated: ${user.id}")
        user.subscriptionState = SubscriptionState.ACTIVE
        userService.save(user)
    }
}