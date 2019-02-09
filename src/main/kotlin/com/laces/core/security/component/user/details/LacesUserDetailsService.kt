package com.laces.core.security.component.user.details

import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserRepository
import org.springframework.stereotype.Service


@Service
class LacesUserDetailsService(
        val userRepository: UserRepository
) {
    fun createUserDetails(user: User): LacesUserDetails{
        return LacesUserDetails(user.subscriptionState)
    }
}