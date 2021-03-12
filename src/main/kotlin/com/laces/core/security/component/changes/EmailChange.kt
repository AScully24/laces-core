package com.laces.core.security.component.changes

import com.laces.core.jpa.BaseEntity
import com.laces.core.security.component.expireInMinutes
import com.laces.core.security.component.token.HasExpiryToken
import com.laces.core.security.component.user.User
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class EmailChange(

    val newEmail: String = "invalid-email@prism-conversion.com",
    override val token: String = "N/A",

    @JoinColumn
    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    val user: User = User(username = "empty"),

    override val expiryDate: Date = expireInMinutes(30)
): HasExpiryToken, BaseEntity()
