package com.laces.core.responses

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class UserCustomerStripeIdException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.CONFLICT)
class UserNameExistsException(userName: String) : RuntimeException("User name exists: $userName")

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserSubscriptionStripeIdException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.NOT_FOUND)
class CurrentUserNotFoundException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.FORBIDDEN)
class UserAccountExpiredException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserRegistrationTokenException(token: String) : RuntimeException("Token does not exists: $token")

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.CONFLICT)
class EmailExistsException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidEmailException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EmptyPasswordException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PasswordMismatchException(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserSubscriptionNotCancelled(exception: String) : RuntimeException(exception)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserSubscriptionCancelPending(exception: String) : RuntimeException(exception)

