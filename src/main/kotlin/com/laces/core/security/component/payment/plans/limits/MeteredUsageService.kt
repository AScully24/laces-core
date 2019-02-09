package com.laces.core.security.component.payment.plans.limits

interface MeteredUsageService {
    fun incrementUsage(subscriptionItemId: String, quantity: Int)
    fun retrieveCurrentUsage()
}