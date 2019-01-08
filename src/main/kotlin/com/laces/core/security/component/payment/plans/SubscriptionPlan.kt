package com.laces.core.security.component.payment.plans

class SubscriptionPlan {
    var name: String = ""
    var recommended = false
    var stripeId: String = ""
    var meteredStripeId: String? = null
    var price = ""
    var features: List<String> = mutableListOf()
}