package com.laces.core.landing.contact

import com.laces.core.email.EmailService
import com.laces.core.responses.InvalidEmailException
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContactService(
        private val emailService: EmailService,
        @Value("\${laces.security.registration.from-email}")
        private val infoEmail: String,
        val emailValidator: EmailValidator = EmailValidator.getInstance(false)
) {

    fun sendUserEnquiry(firstName: String, secondName: String, userEmail: String,  userSubject: String, message: String) {

        if (!emailValidator.isValid(userEmail)) {
            throw InvalidEmailException("Email is not in a valid format: $userEmail")
        }

        val subject = "ENQUIRY - $userSubject - $firstName $secondName"
        val newMessage = "$userEmail\n\n$message"
        emailService.sendSimpleMessage(infoEmail, infoEmail, subject, newMessage)
    }
}