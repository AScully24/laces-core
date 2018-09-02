package com.laces.core.security.component.register

import com.laces.core.jpa.BaseEntity
import com.laces.core.security.component.user.User
import org.apache.commons.lang3.time.DateUtils
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class RegisterToken (

        var token: String = "N/A",

        @JoinColumn
        @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        val user: User = User("empty"),

        var expiryDate: Date = calculateExpiryDate()
) :  BaseEntity() {

    companion object {
        private const val EXPIRATION_TIME_IN_DAYS = 7

        fun calculateExpiryDate(): Date {
            var toReturn = Date(Calendar.getInstance().time.time)
            toReturn = DateUtils.setHours(toReturn,0)
            toReturn = DateUtils.setMinutes(toReturn,0)
            toReturn = DateUtils.setSeconds(toReturn,0)
            toReturn = DateUtils.setMilliseconds(toReturn,0)
            toReturn = DateUtils.addDays(toReturn, EXPIRATION_TIME_IN_DAYS)
            return toReturn
        }
    }
}