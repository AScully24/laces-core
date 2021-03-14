package com.laces.core.security.component.changes.password

import com.laces.core.security.component.token.HasExpiryTokenRepository
import com.laces.core.security.component.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetRepository : JpaRepository<PasswordReset, Long>, HasExpiryTokenRepository<PasswordReset, Long> {
    fun deleteByUser(user: User)
}