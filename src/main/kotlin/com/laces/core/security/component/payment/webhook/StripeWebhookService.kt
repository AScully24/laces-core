package com.laces.core.security.component.payment.webhook

import com.laces.core.security.component.payment.webhook.handlers.WebhookEvent
import com.laces.core.security.component.payment.webhook.handlers.WebhookProcessor
import com.stripe.Stripe
import com.stripe.model.Event
import com.stripe.net.Webhook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class StripeWebhookService(
        @Value("\${app.stripe.secret}")
        secret: String,

        @Value("\${app.stripe.webhook.signing-secret}")
        val signingSecret: String
) {
    init { Stripe.apiKey = secret }

    companion object {
        private val LOG = LoggerFactory.getLogger(StripeWebhookService::class.java)
    }

    @Autowired(required = false)
    var webhookProcessors: List<WebhookProcessor>? = null

    @Transactional
    fun processJsonEventString(stripeSignature: String, jsonEvent: String) {
        val event = Webhook.constructEvent(jsonEvent, stripeSignature, signingSecret)
        LOG.info("Processing event: ${event.type} - ${event.id}")
        webhookProcessors
                ?.filter { isProcessorForEvent(it, event) }
                ?.forEach { it.process(event) }

    }

    private fun isProcessorForEvent(it: WebhookProcessor, event: Event) =
            AnnotationUtils.findAnnotation(it::class.java, WebhookEvent::class.java)?.type == event.type
}