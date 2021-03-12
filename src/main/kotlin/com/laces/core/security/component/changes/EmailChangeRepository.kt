package com.laces.core.security.component.changes

import com.laces.core.security.component.token.HasExpiryTokenRepository
import com.laces.core.security.component.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailChangeRepository : JpaRepository<EmailChange, Long>, HasExpiryTokenRepository<EmailChange, Long> {
    fun deleteByUser(user: User)
}