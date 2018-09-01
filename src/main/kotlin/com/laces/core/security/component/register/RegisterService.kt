package com.laces.core.security.component.register

import com.laces.core.email.EmailService
import com.laces.core.responses.UserRegistrationTokenException
import com.laces.core.security.component.passkey.KeyGeneratorService
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RegisterService {

    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterService::class.java)
    }

    @Autowired
    lateinit var registerTokenRepository: RegisterTokenRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var keyGeneratorService : KeyGeneratorService

    @Autowired
    lateinit var emailService : EmailService

    @Value("\${app.url}")
    lateinit var appUrl : String

    @Transactional
    fun registerNewUser(newUser: NewUser): User {
        userService.validateNewUser(newUser)

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

    @Transactional
    fun completeRegistration(token: String) {

        if (!registerTokenRepository.existsByToken(token)){
            throw UserRegistrationTokenException(token)
        }

        val registerToken = registerTokenRepository.findByToken(token)
        registerToken.user.isActive = true
        userService.save(registerToken.user)
    }


    @Transactional
    @Scheduled(cron = "\${app.unvalidatedUserCron}")
    fun removeUnvalidatedNewUsersAndRegisterTokens(){
        val today = Date()
        val oldTokens = registerTokenRepository.findAllByExpiryDateLessThan(today)
        val oldUsers = oldTokens
                .map { it.user }
                .filter { !it.isActive }

        registerTokenRepository.delete(oldTokens)
        userService.delete(oldUsers)
    }
}