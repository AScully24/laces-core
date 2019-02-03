package com.laces.core.security.component.payment.plans.user

import com.laces.core.responses.UserSubscriptionStripeIdException
import com.laces.core.security.component.payment.plans.SubscriptionPlanService
import com.laces.core.security.component.user.subscription.SubscriptionState
import com.laces.core.security.component.user.UserService
import org.springframework.stereotype.Service

@Service
class UserPlanService (
        val subscriptionPlanService: SubscriptionPlanService,
        val userService: UserService
){
    // Change the controller that calls this to use a DTO object. this will allow consistency if this ever needs to be changed
    fun getCurrentUserPlan() : UserPlan {
        val user = userService.getCurrentUser()
        val plan = subscriptionPlanService.findSubscriptionPlan(user.planStripeId)
                ?: throw UserSubscriptionStripeIdException("Unable to find subscription ID ${user.planStripeId} for user ${user.username}")
        return UserPlan(plan.name,
                user.subscriptionState == SubscriptionState.CANCEL_PENDING,
                user.subscriptionState)
    }
}