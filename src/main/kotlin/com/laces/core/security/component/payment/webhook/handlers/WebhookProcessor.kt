package com.laces.core.security.component.payment.webhook.handlers

import com.stripe.model.Event

interface WebhookProcessor {
    fun process(event: Event)
}

