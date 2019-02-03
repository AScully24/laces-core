package com.laces.core.security.controllers

import com.laces.core.security.component.user.subscription.SubscriptionState

data class LacesUserDetailsDto (
        val subscriptionState: SubscriptionState,
        val planName: String
)