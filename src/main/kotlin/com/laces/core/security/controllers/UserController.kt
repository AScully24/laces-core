package com.laces.core.security.controllers

import com.laces.core.security.component.changes.email.EmailChangeService
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.details.UserDetailsMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
class UserController(
        private val userService: UserService,
        private val emailChangeService: EmailChangeService,
        private val userDetailsMapper: UserDetailsMapper<*>
) {

    @GetMapping("details")
    fun getUserDetails() : Any? {
        return userDetailsMapper.toDto(userService.getCurrentUserFromDatabase())
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