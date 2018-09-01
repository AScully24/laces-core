package com.laces.core.security.controllers.payment

import com.laces.core.responses.UserSubscriptionStripeIdException
import com.laces.core.security.component.payment.PaymentService
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import com.laces.core.security.component.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
@ConditionalOnProperty("app.stripe.enabled")
class PaymentUserController {

    @Autowired
    lateinit var subscriptionPlanService: SubscriptionPlanService

    @Autowired
    lateinit var paymentService: PaymentService

    @Autowired
    lateinit var userService : UserService

    @GetMapping("plan")
    fun availablePlans(): Map<String, String> {
        val user = userService.getCurrentUser()
        val plan = subscriptionPlanService.findSubscriptionPlan(user.planStripeId)
                ?: throw UserSubscriptionStripeIdException("Unable to find subscription ID: ${user.planStripeId}")

        val message = if (user.subscriptionCancelPending) plan.name + " - Cancel Pending" else plan.name

        return mapOf("message" to message)
    }

    @PostMapping("cancel-subscription")
    fun cancelSubscription(): Map<String, String> {
        val user = userService.getCurrentUser()

        paymentService.cancelUserSubscription(user)

        return mapOf("message" to "subscription cancelled")
    }

    @PostMapping("change-subscription")
    fun changeSubscription(@RequestParam newPlanStripeId: String): Map<String, String> {
        val user = userService.getCurrentUser()

        paymentService.changeUserSubscription(user,newPlanStripeId)

        return mapOf("message" to "subscription changed")
    }
}