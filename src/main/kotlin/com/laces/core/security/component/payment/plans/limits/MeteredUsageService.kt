package com.laces.core.security.component.payment.plans.limits

interface MeteredUsageService {
    fun incrementUsage(subscriptionItemId: String, quantity: Long)
    fun retrieveCurrentUsage()
}