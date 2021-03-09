package com.laces.core.security.controllers

import com.laces.core.security.component.payment.plans.user.UserPlanService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/user")
class UserController(
        private val planService: UserPlanService
) {

    @GetMapping("details")
    fun getUserDetails() : LacesUserDetailsDto {
        val (user, plan) = planService.getCurrentUserPlan()
        return LacesUserDetailsDto(
            user.subscriptionState,
            plan.planName,
            user.additionalInfo?.toDto()
        )
    }
}