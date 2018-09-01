package com.laces.core.security.component.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User,Long> {
    fun findByUsername(username: String): User
    fun findBySubscriptionStripeId(subscriptionStripeId: String): User
    fun existsByUsername(userName:String): Boolean
}