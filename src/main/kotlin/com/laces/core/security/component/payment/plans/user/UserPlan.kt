package com.laces.core.security.component.payment.plans.user

import com.laces.core.security.component.user.SubscriptionState

data class UserPlan (
        val planName : String = "",
        val subscriptionCancelPending: Boolean = false,
        val subscriptionState: SubscriptionState = SubscriptionState.ACTIVE)