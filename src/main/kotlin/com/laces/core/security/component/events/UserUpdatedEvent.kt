package com.laces.core.security.component.events

import com.laces.core.security.component.user.User
import org.springframework.context.ApplicationEvent

class UserUpdatedEvent(source: Any, val updatedUser: User) : ApplicationEvent(source)