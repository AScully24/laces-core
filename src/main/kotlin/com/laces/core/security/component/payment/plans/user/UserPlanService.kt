package com.laces.core.security.component.payment.plans.user

import com.laces.core.responses.UserSubscriptionStripeIdException
import com.laces.core.security.component.payment.plans.SubscriptionPlan
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import com.laces.core.security.component.user.User
import com.laces.core.security.component.user.UserService
import com.laces.core.security.component.user.subscription.SubscriptionState
import org.springframework.stereotype.Service

@Service
class UserPlanService (
        val subscriptionPlanService: SubscriptionPlanService,
        val userService: UserService
){
    // Change the controller that calls this to use a DTO object. this will allow consistency if this ever needs to be changed
    fun getCurrentUserPlan() : Pair<User, UserPlan> {
        val user = userService.findById(userService.getCurrentUser().id!!) ?: throw RuntimeException("Unable to find user")
        val plan = findPlanForUser(user)
        return Pair(user, UserPlan(plan.name,
            user.subscriptionState == SubscriptionState.CANCEL_PENDING,
            user.subscriptionState))
    }

    fun findPlanForUser(user: User): SubscriptionPlan {
        return findPlanForUserNullable(user)
            ?: throw UserSubscriptionStripeIdException("Unable to find subscription ID ${user.planStripeId} for user ${user.username}")
    }

    fun findPlanForUserNullable(user: User): SubscriptionPlan? {
        return subscriptionPlanService.findSubscriptionPlan(user.planStripeId)
    }

}