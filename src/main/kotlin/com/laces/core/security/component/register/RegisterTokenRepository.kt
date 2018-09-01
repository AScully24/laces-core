package com.laces.core.security.component.register

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegisterTokenRepository : JpaRepository<RegisterToken,Long> {
    fun findByToken(token: String): RegisterToken

    fun existsByToken(token: String): Boolean

    fun findAllByExpiryDateLessThan(date: Date): List<RegisterToken>

}