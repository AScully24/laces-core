package com.laces.core.security.component.user.subscription

import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.SubscriptionState.*
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserSubscriptionStatusService(
        val userService: UserService
) {
    @Transactional
    fun cancelPendingUserSubscription(user: User) {
        userService.save(user.copy(subscriptionState = CANCEL_PENDING))
    }

    @Transactional
    fun setUserSubscriptionToUnpaid(user: User) {
        val updatedUser = user.copy(subscriptionState = UNPAID)
        userService.save(updatedUser)
        userService.expireUserSessions(user)
    }

    @Transactional
    fun setUserSubscriptionToActive(user: User) {
        userService.save(user.copy(subscriptionState = ACTIVE))
    }

}