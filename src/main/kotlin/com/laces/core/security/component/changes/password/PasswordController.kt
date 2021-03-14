package com.laces.core.security.component.changes.password

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/password")
class PasswordController(
    private val passwordChangeService: PasswordChangeService
) {
    @PostMapping("change")
    fun changePassword(@RequestBody passwordChangeDto: PasswordChangeDto) {
        val (newPassword, confirmNewPassword, currentPassword) = passwordChangeDto
        passwordChangeService.passwordChange(newPassword, confirmNewPassword, currentPassword)
    }

    @PostMapping("reset/request")
    fun requestPasswordReset(@RequestBody passwordChangeDto: RequestPasswordResetDto) {
        val (email) = passwordChangeDto
        passwordChangeService.requestPasswordReset(email)
    }

    @PostMapping("reset/confirmation")
    fun confirmPasswordReset(@RequestBody dto: ConfirmPasswordResetDto) {
        val (newPassword, confirmNewPassword, token) = dto
        passwordChangeService.confirmPasswordReset(token, newPassword, confirmNewPassword)
    }
}

