package com.laces.core.security.component.payment.webhook.handlers.implementations

import com.laces.core.security.component.payment.webhook.handlers.ACTIVE
import com.laces.core.security.component.payment.webhook.handlers.UNPAID
import com.laces.core.security.component.payment.webhook.handlers.WebhookEvent
import com.laces.core.security.component.payment.webhook.handlers.WebhookProcessor
import com.laces.core.security.component.user.subscription.SubscriptionState
import com.laces.core.security.component.user.subscription.SubscriptionState.CANCELLED
import com.laces.core.security.component.user.subscription.SubscriptionState.CANCEL_PENDING
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.UserSubscriptionStatusService
import com.stripe.model.Event
import com.stripe.model.Subscription
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
@Order(0)
@WebhookEvent("customer.subscription.updated")
class SubscriptionUpdatedWebhook(
        val userService: UserService,
        val userSubscriptionStatusService: UserSubscriptionStatusService
) : WebhookProcessor {

    companion object {
        private val LOG = LoggerFactory.getLogger(SubscriptionUpdatedWebhook::class.java)
    }

    override fun process(event: Event) {
        val subscription: Subscription = event.data.`object` as Subscription
        val user = userService.getUserBySubscription(subscription.id)
        if(user == null){
            LOG.error("Can't process event.",event)
            throw RuntimeException("Unable to process event.")
        }
        when {
            isSetToCancelledPending(subscription, user) -> userSubscriptionStatusService.cancelPendingUserSubscription(user)
            subscription.status == UNPAID -> userSubscriptionStatusService.setUserSubscriptionToUnpaid(user)
            isSetToActive(subscription, user) -> userSubscriptionStatusService.setUserSubscriptionToActive(user)
        }
    }

    // Only update to active if the user is not active
    private fun isSetToActive(subscription: Subscription, user: User) =
            subscription.status == ACTIVE &&
                    !subscription.cancelAtPeriodEnd &&
                    user.subscriptionState != SubscriptionState.ACTIVE


    private fun isSetToCancelledPending(subscription: Subscription, user: User): Boolean {
        return subscription.cancelAtPeriodEnd &&
                (user.subscriptionState != CANCEL_PENDING || user.subscriptionState != CANCELLED)
    }
}