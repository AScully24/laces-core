package com.laces.core.security.component.payment.webhook.handlers.implementations

import com.laces.core.security.component.payment.webhook.handlers.WebhookEvent
import com.laces.core.security.component.payment.webhook.handlers.WebhookProcessor
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.stripe.model.Event
import com.stripe.model.Subscription
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Order(0)
@WebhookEvent("customer.subscription.updated")
class InvoicePaidWebhook(
    val userService: UserService
) : WebhookProcessor {

    @Transactional
    override fun process(event: Event) {
        val subscription = event.data.`object` as Subscription
        val user: User = userService.getUserBySubscription(subscription.id) ?: throw RuntimeException("Unable to process webhook event. Customer ID not found $event")
        val updatedUser = user.copy(nextBillingDate = subscription.currentPeriodEnd)
        userService.save(updatedUser)
    }
}