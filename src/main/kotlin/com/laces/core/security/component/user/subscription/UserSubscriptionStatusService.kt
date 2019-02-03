package com.laces.core.security.component.user.subscription

import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserSubscriptionStatusService(
        val userService: UserService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(UserSubscriptionStatusService::class.java)
    }

    @Transactional
    fun cancelPendingUserSubscription(user: User) {
        LOG.info("Cancelling user subscription: ${user.id}")
        user.subscriptionState = SubscriptionState.CANCEL_PENDING
        userService.save(user)
    }

    @Transactional
    fun setUserSubscriptionToUnpaid(user: User) {
        LOG.info("User subscription not paid: ${user.id}")
        user.subscriptionState = SubscriptionState.UNPAID
        userService.save(user)
        userService.expireUserSessions(user)
    }

    @Transactional
    fun setUserSubscriptionToActive(user: User) {
        LOG.info("User subscription activated: ${user.id}")
        user.subscriptionState = SubscriptionState.ACTIVE
        userService.save(user)
    }

}