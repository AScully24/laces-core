package com.laces.core.security.component.user

import com.laces.core.jpa.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class  User (
        @Column(nullable = false,unique = true) val username: String = ""
    ) :  BaseEntity(){

    var password = ""
    var apiKey = ""
    var isActive = true
    var customerStripeId = ""
    var subscriptionStripeId = ""
    var planStripeId = ""
    var subscriptionActive = false
    var subscriptionCancelPending = false
}