package com.laces.core.security.component.user

import com.laces.core.responses.CurrentUserNotFoundException
import com.laces.core.responses.UserNameExistsException
import com.laces.core.security.component.passkey.KeyGeneratorService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val keyGeneratorService: KeyGeneratorService
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

        val user = User(newUser.username)
        user.password = passwordEncoder.encode(newUser.password)
        user.apiKey = keyGeneratorService.generateNewPassKey()
        user.isActive = isActive
        return save(user)

    }

    @Transactional
    fun findByUsername(userName: String): User {
        return userRepository.findByUsername(userName)
    }

    fun isUserLoggedIn(): Boolean {
        getCurrentUser()
        return true
    }

    fun getCurrentUser(): User {
        val auth = SecurityContextHolder.getContext().authentication
                ?: throw CurrentUserNotFoundException("Unable to find current user")

        val principal = auth.principal as? MyUserPrincipal
                ?: throw CurrentUserNotFoundException("Unable to find current user")

        return findByUsername(principal.username)
    }

    fun existsByName(userName: String): Boolean {
        return userRepository.existsByUsername(userName)
    }

    fun getUserBySubscription(subscriptionId: String): User {
        return userRepository.findBySubscriptionStripeId(subscriptionId)
    }

    fun generateNewApiKeyForCurrentUser(): String {
        return generateNewUserApiKey(getCurrentUser())
    }

    private fun generateNewUserApiKey(user: User): String {
        user.apiKey = keyGeneratorService.generateNewPassKey()
        save(user)
        return user.apiKey
    }
}