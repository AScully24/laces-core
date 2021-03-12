package com.laces.core.security.component.register

import com.laces.core.security.component.token.HasExpiryTokenRepository
import com.laces.core.security.component.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegisterTokenRepository : JpaRepository<RegisterToken, Long>, HasExpiryTokenRepository<RegisterToken, Long> {
    fun deleteByUser(user: User)
}