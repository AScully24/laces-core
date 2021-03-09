package com.laces.core.security.component.payment.plans

import com.laces.core.security.component.user.NewUser

class NewSubscription(
    val token: String? = null,
    val newUser: NewUser,
    val productStripeId: String

)