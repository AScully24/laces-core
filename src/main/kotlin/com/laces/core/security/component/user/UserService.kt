package com.laces.core.security.component.user

import com.laces.core.email.isValidEmail
import com.laces.core.responses.CurrentUserNotFoundException
import com.laces.core.responses.EmailExistsException
import com.laces.core.responses.UserNameExistsException
import com.laces.core.security.component.user.spring.MyUserPrincipal
import com.laces.core.security.component.user.subscription.SubscriptionState.ACTIVE
import com.laces.core.security.component.user.subscription.SubscriptionState.AWAITING_CONFIRMATION
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val sessionRegistry: SessionRegistry
) {

    @Transactional
    fun delete(users: List<User>) {
        userRepository.deleteAll(users)
    }

    @Transactional
    fun save(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun saveNewUser(newUser: NewUser, isActive: Boolean): User {

        if (userRepository.existsByUsername(newUser.username)) {
            throw UserNameExistsException(newUser.username)
        }

        val user = User(username = newUser.username,
                password = passwordEncoder.encode(newUser.password),
                subscriptionState = if (isActive) ACTIVE else AWAITING_CONFIRMATION,
                additionalInfo = newUser.additionalInfo
        )
        return save(user)

    }

    fun isUserLoggedIn(): Boolean {
        try {
            getCurrentUser()
        } catch (e: CurrentUserNotFoundException) {
            return false
        }
        return true
    }

    fun getCurrentUser(): User {
        val auth = SecurityContextHolder.getContext().authentication
                ?: throw CurrentUserNotFoundException("Unable to find current user")

        val principal = auth.principal as? MyUserPrincipal
                ?: throw CurrentUserNotFoundException("Unable to find current user")

        return principal.user
    }

    fun getCurrentUserFromDatabase() : User{
        return getCurrentUser().id?.let(::findById) ?: throw CurrentUserNotFoundException("Unable to find current user")
    }

    fun existsByName(userName: String): Boolean {
        return userRepository.existsByUsername(userName)
    }

    fun findByUsername(userName: String): User? {
        return userRepository.findByUsername(userName)
    }

    fun getUserBySubscription(subscriptionId: String): User? {
        return userRepository.findBySubscriptionStripeId(subscriptionId)
    }

    fun findById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }

    fun expireUserSessions(user: User) {
        sessionRegistry.allPrincipals
                .filterIsInstance(MyUserPrincipal::class.java)
                .filter { it.username == user.username }
                .flatMap { sessionRegistry.getAllSessions(it, true) }
                .forEach { it.expireNow() }

    }

    fun validateNewEmail(userName: String) {
        isValidEmail(userName)

        if (existsByName(userName)) {
            throw EmailExistsException("Email already exists: $userName")
        }
    }
}