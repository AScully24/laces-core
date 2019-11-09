package com.laces.core.security.component.register

import com.laces.core.email.EmailService
import com.laces.core.responses.*
import com.laces.core.security.component.passkey.KeyGeneratorService
import com.laces.core.security.component.payment.PaymentService
import com.laces.core.security.component.payment.plans.NewSubscription
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.SubscriptionState
import com.laces.core.security.component.user.subscription.SubscriptionState.ACTIVE
import org.apache.commons.lang3.StringUtils
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
        val paymentService: PaymentService,

        @Value("\${app.url}")
        val appUrl: String
) {

    @Autowired(required = false)
    var newUserAdapters: List<NewUserAdapter>? = null

    @Autowired(required = false)
    var userRemovalAdapters: List<UserRemovalAdapter>? = null

    @Autowired(required = false)
    var userConfirmedAdapters: List<UserConfirmedAdapter>? = null

    @Autowired
    var additionalInfoValidator : AdditionalInfoValidator? = null


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
            emailService.sendSimpleMessageFromRegistration(newUser.username, "Registration Confirmation",
                    "Please follow the following link to complete your registrations.\n${appUrl}register-confirmation/$passKey")

        } catch (e: Exception) {
            LOG.error("Unable to send email: ", e)
        }

        newUserAdapters?.forEach { catchAdapterException { it.action(user) } }

        return user

    }

    fun registerUserWithSubscription(userSubscription: NewSubscription) {
        val user = registerNewUser(userSubscription.newUser)

        val stripeSubscription = paymentService.createCustomerAndSignUpToSubscription(user,
                userSubscription.token,
                userSubscription.productStripeId,
                SubscriptionState.AWAITING_CONFIRMATION
        )

        newUserAdapters?.forEach {
            catchAdapterException { it.action(user, userSubscription, stripeSubscription) }
        }
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

        additionalInfoValidator?.validate(newUser.additionalInfo)
    }

    @Transactional
    fun completeRegistration(token: String) {

        if (!registerTokenRepository.existsByToken(token)) {
            throw UserRegistrationTokenException(token)
        }

        val registerToken = registerTokenRepository.findByToken(token)
        val user = registerToken.user
        val confirmedUser = userService.save(user.copy(subscriptionState = ACTIVE))

        userConfirmedAdapters?.forEach { catchAdapterException { it.action(confirmedUser) } }
    }

    @Transactional
    @Scheduled(cron = "\${laces.unvalidatedUserCron}")
    fun removeUnvalidatedNewUsersAndRegisterTokens() {
        val today = Date()
        val oldTokens = registerTokenRepository.findAllByExpiryDateLessThan(today)
        val oldUsers = oldTokens
                .map { it.user }
                .filter { it.subscriptionState == SubscriptionState.AWAITING_CONFIRMATION }

        registerTokenRepository.deleteAll(oldTokens)

        userRemovalAdapters?.forEach { catchAdapterException { it.action(oldUsers) } }

        userService.delete(oldUsers)
    }

    private fun catchAdapterException(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            LOG.error("Adapter Failure", e)
        }
    }
}