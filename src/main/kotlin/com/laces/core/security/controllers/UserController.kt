package com.laces.core.security.controllers

import com.laces.core.security.component.changes.EmailChangeService
import com.laces.core.security.component.payment.plans.user.UserPlanService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
class UserController(
        private val planService: UserPlanService,
        private val emailChangeService: EmailChangeService
) {

    @GetMapping("details")
    fun getUserDetails() : LacesUserDetailsDto {
        val (user, plan) = planService.getCurrentUserPlan()
        return LacesUserDetailsDto(
            user.username,
            user.subscriptionState,
            plan.planName,
            user.additionalInfo?.toDto()
        )
    }

    @PostMapping("email/change-request")
    fun requestEmailChange(@RequestBody requestEmailChangeDto: RequestEmailChangeDto){
        val (newEmail, confirmNewEmail, password) = requestEmailChangeDto
        emailChangeService.requestEmailChange(newEmail,confirmNewEmail, password)
    }

    @PostMapping("email/change-confirmation")
    fun confirmEmailChange(@RequestBody dto: ConfirmEmailChangeDto){
        emailChangeService.confirmEmailChange(dto.token)
    }
}

