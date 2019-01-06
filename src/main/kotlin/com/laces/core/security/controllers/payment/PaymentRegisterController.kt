package com.laces.core.security.controllers.payment

import com.laces.core.responses.ResourceNotFoundException
import com.laces.core.security.component.payment.PaymentService
import com.laces.core.security.component.payment.plans.NewSubscription
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import com.laces.core.security.component.register.RegisterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
@ConditionalOnProperty("app.stripe.enabled")
class PaymentRegisterController(
        val registerService: RegisterService,
        val subscriptionPlanService: SubscriptionPlanService
) {

    @PutMapping(value = ["register"],consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun registerSubscription(@RequestBody userSubscription: NewSubscription): Map<String, String> {

        if(!subscriptionPlanService.planStripeIdExists(userSubscription.productStripeId)){
            throw ResourceNotFoundException("Unable to find product ID: ${userSubscription.productStripeId}")
        }

        registerService.registerUserWithSubscription(userSubscription)

        return mapOf("success" to "User was successfully registered.")
    }
}