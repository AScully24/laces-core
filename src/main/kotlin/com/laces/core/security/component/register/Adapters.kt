package com.laces.core.security.component.register

import com.laces.core.security.component.payment.plans.NewSubscription
import com.laces.core.security.component.user.User
import com.stripe.model.Subscription
import org.slf4j.LoggerFactory

//Adapters should run at the end of their respective methods to give devs the option for additional actions.
interface NewUserAdapter{
    companion object {
        private val LOG = LoggerFactory.getLogger(NewUserAdapter::class.java)
    }

    fun action(newUser: User){
        LOG.info("Action not implemented.")
    }

    fun action(newUser: User,userSubscription: NewSubscription, stripeSubscription : Subscription){
        LOG.info("Action not implemented.")
    }
}

interface UserConfirmedAdapter{
    fun action(confirmedUser: User)
}

interface UserRemovalAdapter{
    fun action(toBeRemovedUsers: List<User>)
}