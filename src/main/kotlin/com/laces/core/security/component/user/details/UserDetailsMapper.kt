package com.laces.core.security.component.user.details

import com.laces.core.security.component.user.User

interface UserDetailsMapper<R> {

    fun toDto(user: User) : R

}