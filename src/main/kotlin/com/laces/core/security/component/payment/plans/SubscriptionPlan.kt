package com.laces.core.security.component.payment.plans

class SubscriptionPlan{
    var name : String = ""
    var recommended = false
    var stripeId : String = ""
    var price = ""
    var features : List<String> = mutableListOf()
}