package com.laces.core.security.component.user

import com.laces.core.jpa.HasId
import com.laces.core.security.component.user.subscription.SubscriptionState
import kotlinx.serialization.Serializable
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "users")
@Serializable
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

    // TODO: Being lazy here. Need to convert this to a proper date at some point.
    val nextBillingDate: Long = 0L,

    @OneToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER)
    val additionalInfo: AdditionalInfo? = null

) : HasId, java.io.Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }
}