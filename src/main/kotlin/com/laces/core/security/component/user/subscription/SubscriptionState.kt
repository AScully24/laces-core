package com.laces.core.security.component.user.subscription

enum class SubscriptionState(val display: String, val description: String) {
    ACTIVE("Active", "Your account is active"),
    AWAITING_CONFIRMATION("Awaiting Confirmation","Please confirm your email to have full access to your account"),
    CANCEL_PENDING("Cancel Pending", "Your account will be cancelled at the end of your billing period"),
    CANCELLED("Cancelled","Your account is not no longer active"),
    UNPAID("Unpaid","You have missed a payment. Please update your payment details for full access to your account.")
}