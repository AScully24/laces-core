package com.laces.core.security.component.payment.plans

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties(prefix="plans")
@ConditionalOnProperty("app.stripe.enabled")
class SubscriptionPlanService {

    val subscriptionPlans : List<SubscriptionPlan> = ArrayList()

    fun availableSubscriptionPlan() : List<SubscriptionPlan>{
        return subscriptionPlans
    }

    fun findSubscriptionPlan(stripId : String) : SubscriptionPlan? {
        return subscriptionPlans.find { it.stripeId == stripId }
    }

    fun planStripeIdExists(stripId : String) : Boolean {
        return subscriptionPlans.any { it.stripeId == stripId }
    }

}