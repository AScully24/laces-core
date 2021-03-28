package com.laces.core.security.component.user.subscription

import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(UNAUTHORIZED)
class AccountExpiredException : RuntimeException("The account has expired. Please reactivate your account to use this resource.")
