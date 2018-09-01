package com.laces.core.security.component.user

import com.laces.core.responses.*
import com.laces.core.security.component.passkey.KeyGeneratorService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService {

    @Autowired
    lateinit var userRepository : UserRepository

    @Autowired
    lateinit var passwordEncoder : PasswordEncoder

    @Autowired
    lateinit var keyGeneratorService: KeyGeneratorService

    @Transactional
    fun delete (users:List<User>) {
        userRepository.delete(users)
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

    fun isUserLoggedIn() : Boolean{
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

    fun validateNewUser(newUser: NewUser) {

        if(existsByName(newUser.username)){
            throw EmailExistsException("Email already exists: ${newUser.username}")
        }

        if(StringUtils.isBlank(newUser.password)){
            throw EmptyPasswordException("Password cannot be blank.")
        }

        if(newUser.password != newUser.confirmPassword){
            throw PasswordMismatchException("Passwords do not match.")
        }
    }

    fun existsByName(userName:String) : Boolean{
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