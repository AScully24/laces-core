package com.laces.core.security.component.payment

import com.laces.core.responses.UserSubscriptionCancelPending
import com.laces.core.responses.UserSubscriptionNotCancelled
import com.stripe.model.Subscription
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StripeSubscriptionValidator {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StripeSubscriptionValidator::class.java)
    }

    fun checkCancelPending(subscription: Subscription) {
        if (!isCancelledPending(subscription)) {
            LOGGER.warn("Cannot cancel subscription: ${subscription.id}")
            throw UserSubscriptionNotCancelled("Subscription already active")
        }

        if (!subscription.cancelAtPeriodEnd) {
            throw UserSubscriptionNotCancelled("Subscription has been completely cancelled. You must start a new subscription")
        }
    }

    private fun isCancelledPending(subscription: Subscription): Boolean {
        return subscription.canceledAt != null && subscription.cancelAtPeriodEnd && subscription.endedAt == null
    }

    fun checkCancelled(subscription: Subscription) {
        if (isCancelledPending(subscription)) {
            throw UserSubscriptionCancelPending("Subscription has not been completely cancelled. Reactivate or change subscriptions instead.")
        }

        if (!isCancelled(subscription)) {
            throw UserSubscriptionNotCancelled("Subscription has not been cancelled. Use change subscriptions instead")
        }
    }

    private fun isCancelled(subscription: Subscription) = subscription.endedAt != null
}