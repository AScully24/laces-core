package com.laces.core.security.component.changes

import com.laces.core.email.EmailService
import com.laces.core.responses.InvalidEmailException
import com.laces.core.responses.InvalidPasswordException
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
class EmailChangeService(
    private val userService: UserService,
    private val emailChangeRepository: EmailChangeRepository,
    private val randomKeyService: RandomKeyService,
    private val emailService: EmailService,
    private val passwordEncoder: PasswordEncoder,
    private val applicationEventPublisher: ApplicationEventPublisher,

    @Value("\${app.url}")
    val appUrl: String
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(EmailChangeService::class.java)
    }

    @Transactional
    fun requestEmailChange(newEmail: String, confirmNewEmail: String, password: String) {

        userService.validateNewEmail(newEmail)

        if(newEmail != confirmNewEmail){
            throw InvalidEmailException("Emails do not match.")
        }

        val user = userService.getCurrentUser()

        if(!passwordEncoder.matches(password, user.password)){
            throw InvalidPasswordException()
        }

        val passKey = randomKeyService.generateRandomKey()
        emailChangeRepository.deleteByUser(user)
        emailChangeRepository.save(EmailChange(newEmail,passKey, user))

        try {
            emailService.sendSimpleMessageFromRegistration(
                newEmail, "Email Change Request",
                "Please select the following link to confirm the change to your change your email.\n${appUrl}account/email/change-confirmation/$passKey"
            )

        } catch (e: Exception) {
            LOG.error("Unable to send email: ", e)
        }
    }


    @Transactional
    fun confirmEmailChange(token: String) {
        val emailChange =
            emailChangeRepository.findByToken(token) ?: throw ResourceNotFoundException("Unable to find token: $token")
        val newEmail = emailChange.newEmail
        val user = emailChange.user

        userService.validateNewEmail(newEmail)

        val updatedUser = user.copy(username = newEmail)

        userService.save(updatedUser)
        emailChangeRepository.delete(emailChange)

        applicationEventPublisher.publishEvent(UserUpdatedEvent(this, updatedUser))
    }

    @Transactional
    @Scheduled(cron = "\${laces.unusedEmailChangeCron}")
    fun removeUnvalidatedNewUsersAndRegisterTokens() {
        val now = Date()
        val oldTokens = emailChangeRepository.findAllByExpiryDateLessThan(now)
        emailChangeRepository.deleteAll(oldTokens)
    }


}