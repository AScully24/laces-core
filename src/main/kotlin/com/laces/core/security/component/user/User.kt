package com.laces.core.security.component.user

import com.laces.core.jpa.HasId
import com.laces.core.security.component.user.subscription.SubscriptionState
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "users")
data class User(

        @Id
        @GeneratedValue(strategy = IDENTITY)
        override var id: Long? = null,

        @Column(nullable = false, unique = true)
        val username: String = "",

        val password: String = "",
        @Enumerated(EnumType.STRING)
        val subscriptionState: SubscriptionState = SubscriptionState.AWAITING_CONFIRMATION,

        val customerStripeId: String = "",
        val subscriptionStripeId: String = "",
        val subscriptionItemId: String = "",
        val planStripeId: String = "",
        val meteredStripeId: String? = null,
        @OneToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER)
        val additionalInfo: AdditionalInfo? = null

) : HasId