package com.laces.core.security.controllers

import com.laces.core.security.component.payment.plans.user.UserPlanService
import com.laces.core.security.component.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/user")
class UserController(
        val userService: UserService,
        val planService: UserPlanService
) {

    @GetMapping("details")
    fun getUserDetails() : LacesUserDetailsDto {
        val currentUser = userService.getCurrentUser()
        return LacesUserDetailsDto(
                currentUser.subscriptionState,
                planService.getCurrentUserPlan().planName,
                currentUser.additionalInfo?.toDto()
        )
    }
}