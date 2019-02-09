package com.laces.core.security.controllers.payment.forms

class ReactivateCancelledForm(
        var newStripeId: String = "",
        var stripeToken: String = ""
)