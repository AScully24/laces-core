package com.laces.core.security.controllers.payment

import com.laces.core.security.component.payment.PaymentService
import com.laces.core.security.component.payment.plans.user.UserPlan
import com.laces.core.security.component.payment.plans.user.UserPlanService
import com.laces.core.security.component.user.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
@ConditionalOnProperty("app.stripe.enabled")
class PaymentUserController(
        val paymentService: PaymentService,
        val userService: UserService,
        val userPlanService: UserPlanService

) {
    @GetMapping("plan")
    fun availablePlans(): UserPlan {
        return userPlanService.getCurrentUserPlan()
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

        paymentService.changeUserSubscription(user, newPlanStripeId)

        return mapOf("message" to "subscription changed")
    }

    @PostMapping("reactivate-subscription")
    fun reactivateSubscription(): Map<String, String> {
        val user = userService.getCurrentUser()

        paymentService.reactivateUserSubscriptionPendingCancellation(user)

        return mapOf("message" to "subscription reactivated")
    }
}