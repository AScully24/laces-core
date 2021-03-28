package com.laces.core.security.controllers.payment

import com.laces.core.security.component.payment.webhook.StripeWebhookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("stripe")
@ConditionalOnProperty("app.stripe.enabled")
class StripeController (
    @Value("\${app.stripe.api-key}")
    private val apiKey: String
        ){

    @Autowired
    lateinit var stripeWebHookService: StripeWebhookService

    @ResponseBody
    @PostMapping(value = ["webhook"], consumes = ["application/json"], produces = ["application/json"])
    fun stripeWebhookEndpoint(@RequestHeader("Stripe-Signature") stripeSignature: String, @RequestBody jsonEvent: String): Map<String, String> {
        stripeWebHookService.processJsonEventString(stripeSignature, jsonEvent)
        return mapOf("message" to "Successfully processed stripe event")
    }

    @GetMapping("api-key")
    fun publicKey() = apiKey
}