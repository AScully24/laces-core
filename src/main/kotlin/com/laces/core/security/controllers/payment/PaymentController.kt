package com.laces.core.security.controllers.payment

import com.laces.core.security.component.payment.plans.SubscriptionPlan
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("payment")
@ConditionalOnProperty("app.stripe.enabled")
class PaymentController {

    @Autowired
    lateinit var subscriptionPlanService: SubscriptionPlanService

    @GetMapping("plans")
    fun availablePlans() : List<SubscriptionPlan> = subscriptionPlanService.availableSubscriptionPlan()

}