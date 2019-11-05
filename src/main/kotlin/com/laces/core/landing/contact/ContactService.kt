package com.laces.core.landing.contact

import com.laces.core.email.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContactService(
        private val emailService: EmailService,
        @Value("\${laces.security.registration.from-email}")
        private val infoEmail: String
) {

    fun sendUserEnquiry(firstName: String, secondName: String, userEmail: String,  userSubject: String, message: String) {
        val subject = "USER ENQUIRY - $firstName $secondName - $userSubject"
        val newMessage = "$userEmail\n\n$message"
        emailService.sendSimpleMessage(infoEmail, infoEmail, subject, newMessage)
    }
}