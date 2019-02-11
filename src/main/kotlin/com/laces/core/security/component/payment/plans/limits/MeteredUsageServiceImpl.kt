package com.laces.core.security.component.payment.plans.limits

import com.laces.core.security.component.user.UserService
import com.stripe.Stripe
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import com.stripe.model.UsageRecord
import com.stripe.net.RequestOptions
import java.time.Instant
import java.util.HashMap
import com.stripe.model.Invoice
import java.time.ZoneOffset


@Service
private class MeteredUsageServiceImpl(
        @Value("\${app.stripe.secret}")
        val secret: String,
        val userService: UserService
) : MeteredUsageService {

    @PostConstruct
    fun init() {
        Stripe.apiKey = secret
    }

    override fun incrementUsage(subscriptionItemId: String, quantity: Int) {

        val usageRecordParams = HashMap<String, Any>()
        usageRecordParams["quantity"] = quantity
        usageRecordParams["timestamp"] = Instant.now().atOffset(ZoneOffset.UTC).toInstant().epochSecond
        usageRecordParams["subscription_item"] = subscriptionItemId
        usageRecordParams["action"] = "increment"

        UsageRecord.create(usageRecordParams, RequestOptions.getDefault())
    }

    override fun retrieveCurrentUsage() {

        val currentUser = userService.getCurrentUser()

        val invoiceParams = HashMap<String, Any>()
        invoiceParams["customer"] = currentUser.customerStripeId
        invoiceParams[" subscription"] = currentUser.planStripeId

        val upcoming = Invoice.upcoming(invoiceParams)

        upcoming.amountDue
        upcoming.amountRemaining
        upcoming.periodStart
        upcoming.periodEnd
    }
}