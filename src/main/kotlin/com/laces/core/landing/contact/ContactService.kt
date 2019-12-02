package com.laces.core.landing.contact

import com.laces.core.email.EmailService
import com.laces.core.email.isValidEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContactService(
        private val emailService: EmailService,

        @Value("\${laces.security.registration.from-email}")
        private val infoEmail: String
) {

    fun sendUserEnquiry(firstName: String, secondName: String, userEmail: String, userSubject: InfoEnquirySubject, message: String) {

        isValidEmail(userEmail)

        val subject = "ENQUIRY - $userSubject - $firstName $secondName"
        val newMessage = "$userEmail\n\n$message"
        emailService.sendSimpleMessage(infoEmail, infoEmail, subject, newMessage)
    }

}