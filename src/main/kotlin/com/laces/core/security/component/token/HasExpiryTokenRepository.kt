package com.laces.core.security.component.token

import java.util.*

interface HasExpiryTokenRepository<E: HasExpiryToken, ID> {
    fun findByToken(token: String): E?
    fun existsByToken(token: String): Boolean
    fun findAllByExpiryDateLessThan(date: Date): List<E>
}