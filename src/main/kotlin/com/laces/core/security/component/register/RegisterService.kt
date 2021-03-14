package com.laces.core.security.component.register

import com.laces.core.email.EmailService
import com.laces.core.responses.ResourceNotFoundException
import com.laces.core.responses.UserRegistrationTokenException
import com.laces.core.security.component.payment.PaymentService
import com.laces.core.security.component.payment.plans.NewSubscription
import com.laces.core.security.component.random.RandomKeyService
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.SubscriptionState
import com.laces.core.security.component.user.subscription.SubscriptionState.ACTIVE
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RegisterService(
    private val registerTokenRepository: RegisterTokenRepository,
    private val userService: UserService,
    private val emailService: EmailService,
    private val paymentService: PaymentService,
    private val randomKeyService: RandomKeyService,

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
    var additionalInfoValidator: AdditionalInfoValidator? = null

    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterService::class.java)
    }

    @Transactional
    fun registerNewUser(newUser: NewUser): User {
        validateNewUser(newUser)

        val user = userService.saveNewUser(newUser, false)

        createNewRegisterToken(user)

        newUserAdapters?.forEach { catchAdapterException { it.action(user) } }

        return user
    }

    @Transactional
    fun refreshRegisterToken(userName: String) {
        val user = userService.findByUsername(userName) ?: throw ResourceNotFoundException("Unable to find username: $userName")
        registerTokenRepository.deleteByUser(user)
        createNewRegisterToken(user)
    }

    private fun createNewRegisterToken(user: User) {
        val passKey = randomKeyService.generateRandomKey()
        registerTokenRepository.save(RegisterToken(passKey, user))
        try {
            emailService.sendSimpleMessageFromRegistration(
                user.username, "Registration Confirmation",
                "Please select the following link to complete your registrations.\n${appUrl}register-confirmation/$passKey"
            )

        } catch (e: Exception) {
            LOG.error("Unable to send email: ", e)
        }
    }

    @Transactional
    fun registerUserWithSubscription(userSubscription: NewSubscription) {
        val user = registerNewUser(userSubscription.newUser)

        val stripeSubscription = paymentService.createCustomerAndSignUpToSubscription(
            user,
            userSubscription.token,
            userSubscription.productStripeId,
            SubscriptionState.AWAITING_CONFIRMATION
        )

        newUserAdapters?.forEach {
            catchAdapterException {
                it.action(user, userSubscription, stripeSubscription)
            }
        }
    }

    fun validateNewUser(newUser: NewUser) {

        userService.validateNewEmail(newUser.username)

        val password = newUser.password
        val confirmPassword = newUser.confirmPassword

        userService.validatePassword(password, confirmPassword)

        additionalInfoValidator?.validate(newUser.additionalInfo)
    }

    @Transactional
    fun completeRegistration(token: String) {

        if (!registerTokenRepository.existsByToken(token)) {
            throw UserRegistrationTokenException(token)
        }

        val registerToken = registerTokenRepository.findByToken(token) ?: throw ResourceNotFoundException("Unable to find token: $token")
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