package com.laces.core.security.component.register

import com.laces.core.email.EmailService
import com.laces.core.responses.*
import com.laces.core.security.component.passkey.KeyGeneratorService
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import org.apache.commons.lang3.StringUtils
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RegisterService(
        val registerTokenRepository: RegisterTokenRepository,
        val userService: UserService,
        val keyGeneratorService: KeyGeneratorService,
        val emailService: EmailService,

        @Value("\${app.url}")
        val appUrl: String
) {

    val emailValidator: EmailValidator = EmailValidator.getInstance(false)

    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterService::class.java)
    }

    @Transactional
    fun registerNewUser(newUser: NewUser): User {
        validateNewUser(newUser)

        val user = userService.saveNewUser(newUser, false)

        val passKey = keyGeneratorService.generateNewPassKey()
        registerTokenRepository.save(RegisterToken(passKey, user))
        try {
            emailService.sendSimpleMessage(newUser.username, "ADT Registration Confirmation",
                    "Please follow the following link to the URL to complete your registrations.\n${appUrl}register-confirmation/$passKey")

        } catch (e: Exception) {
            LOG.error("Unable to send email: ", e)
        }

        return user

    }

    fun validateNewUser(newUser: NewUser) {

        if (userService.existsByName(newUser.username)) {
            throw EmailExistsException("Email already exists: ${newUser.username}")
        }

        if (!emailValidator.isValid(newUser.username)) {
            throw InvalidEmailException("Email is not in a valid format: ${newUser.username}")
        }

        if (StringUtils.isBlank(newUser.password)) {
            throw EmptyPasswordException("Password cannot be blank.")
        }

        if (newUser.password != newUser.confirmPassword) {
            throw PasswordMismatchException("Passwords do not match.")
        }
    }


    @Transactional
    fun completeRegistration(token: String) {

        if (!registerTokenRepository.existsByToken(token)) {
            throw UserRegistrationTokenException(token)
        }

        val registerToken = registerTokenRepository.findByToken(token)
        registerToken.user.isActive = true
        userService.save(registerToken.user)
    }


    @Transactional
    @Scheduled(cron = "\${app.unvalidatedUserCron}")
    fun removeUnvalidatedNewUsersAndRegisterTokens() {
        val today = Date()
        val oldTokens = registerTokenRepository.findAllByExpiryDateLessThan(today)
        val oldUsers = oldTokens
                .map { it.user }
                .filter { !it.isActive }

        registerTokenRepository.delete(oldTokens)
        userService.delete(oldUsers)
    }
}