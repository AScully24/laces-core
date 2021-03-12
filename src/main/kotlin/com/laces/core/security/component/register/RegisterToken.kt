package com.laces.core.security.component.register

import com.laces.core.jpa.BaseEntity
import com.laces.core.security.component.expireInDays
import com.laces.core.security.component.token.HasExpiryToken
import com.laces.core.security.component.user.User
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class RegisterToken (

        override val token: String = "N/A",

        @JoinColumn
        @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        val user: User = User(username = "empty"),

        override val expiryDate: Date = expireInDays(7)
) :  HasExpiryToken, BaseEntity()