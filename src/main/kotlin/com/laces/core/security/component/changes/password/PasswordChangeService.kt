package com.laces.core.security.component.changes.password

import com.laces.core.email.EmailService
import com.laces.core.responses.ResourceNotFoundException
import com.laces.core.security.component.events.UserUpdatedEvent
import com.laces.core.security.component.random.RandomKeyService
import com.laces.core.security.component.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class PasswordChangeService(
    private val userService: UserService,
    private val passwordResetRepository: PasswordResetRepository,
    private val passwordEncoder: PasswordEncoder,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val randomKeyService: RandomKeyService,
    private val emailService: EmailService,
    @Value("\${app.url}")
    val appUrl: String
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(PasswordChangeService::class.java)
    }

    @Transactional
    fun passwordChange(newPassword: String, confirmNewPassword: String, currentPassword: String) {

        val user = userService.getCurrentUserFromDatabase()

        userService.confirmPassword(currentPassword, user)
        userService.validatePassword(newPassword, confirmNewPassword)

        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))
        userService.save(updatedUser)

        applicationEventPublisher.publishEvent(UserUpdatedEvent(this, updatedUser))
    }

    @Transactional
    fun requestPasswordReset(email: String) {

        val user = userService.findByUsername(email) ?: return

        val passKey = randomKeyService.generateRandomKey()
        passwordResetRepository.deleteByUser(user)
        passwordResetRepository.save(PasswordReset(passKey, user))

        try {
            emailService.sendSimpleMessageFromRegistration(
                email, "Password Reset Request",
                "Please select the following link to confirm the reset of your password.\n${appUrl}password-reset-confirmation/$passKey"
            )
        } catch (e: Exception) {
            LOG.error("Unable to send email: ", e)
        }
    }


    @Transactional
    fun confirmPasswordReset(token: String, newPassword: String, newPasswordConfirm: String) {

        val passwordReset = passwordResetRepository.findByToken(token) ?: throw ResourceNotFoundException("Unable to find password reset token: $token")
        val user = passwordReset.user

        userService.validatePassword(newPassword, newPasswordConfirm)

        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))

        userService.save(updatedUser)
        passwordResetRepository.delete(passwordReset)

        applicationEventPublisher.publishEvent(UserUpdatedEvent(this, updatedUser))
    }

    @Transactional
    @Scheduled(cron = "\${laces.unusedEmailChangeCron}")
    fun removeUnvalidatedNewUsersAndRegisterTokens() {
        val now = Date()
        val oldTokens = passwordResetRepository.findAllByExpiryDateLessThan(now)
        passwordResetRepository.deleteAll(oldTokens)
    }


}