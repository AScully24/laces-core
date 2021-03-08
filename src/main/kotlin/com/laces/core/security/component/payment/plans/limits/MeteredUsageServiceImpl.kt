package com.laces.core.security.component.payment.plans.limits

import com.laces.core.security.component.user.UserService
import com.stripe.Stripe
import com.stripe.model.Invoice
import com.stripe.model.UsageRecord
import com.stripe.net.RequestOptions
import com.stripe.param.UsageRecordCreateOnSubscriptionItemParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.util.*


@Service
class MeteredUsageServiceImpl(
    @Value("\${app.stripe.secret}")
    secret: String,
    val userService: UserService
) : MeteredUsageService {

    init {
        Stripe.apiKey = secret
    }

    override fun incrementUsage(subscriptionItemId: String, quantity: Long) {

        val params = UsageRecordCreateOnSubscriptionItemParams.builder()
            .setQuantity(quantity)
            .setTimestamp(Instant.now().atOffset(ZoneOffset.UTC).toInstant().epochSecond)
            .setAction(UsageRecordCreateOnSubscriptionItemParams.Action.INCREMENT)
            .build()

        UsageRecord.createOnSubscriptionItem(subscriptionItemId, params, RequestOptions.getDefault())
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