package com.laces.core.email

import com.laces.core.responses.InvalidEmailException
import org.apache.commons.validator.routines.EmailValidator


private val emailValidator: EmailValidator = EmailValidator.getInstance(false)

@Throws(InvalidEmailException::class)
fun isValidEmail(email: String) {
    if (!emailValidator.isValid(email)) {
        throw InvalidEmailException("Email is not in a valid format: $email")
    }
}