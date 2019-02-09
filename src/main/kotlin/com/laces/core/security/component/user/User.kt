package com.laces.core.security.component.user

import com.laces.core.jpa.BaseEntity
import com.laces.core.security.component.user.subscription.SubscriptionState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
data class User(
        @Column(nullable = false, unique = true) val username: String = ""
) : BaseEntity() {
    var password = ""
    var apiKey = ""

    @Enumerated(EnumType.STRING)
    var subscriptionState : SubscriptionState = SubscriptionState.AWAITING_CONFIRMATION
    var customerStripeId = ""
    var subscriptionStripeId = ""
    var subscriptionItemId = ""
    var planStripeId = ""
    var meteredStripeId: String? = null
}