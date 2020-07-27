package com.laces.core.landing.contact

import com.laces.core.email.EmailService
import com.laces.core.email.isValidEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContactService(
        private val emailService: EmailService,

        @Value("\${laces.contact.email-address.info}")
        private val infoEmailAddress: String
) {

    fun sendUserEnquiry(email: String, subject: InfoEnquirySubject, message: String) {

        isValidEmail(email)

        val emailSubject = "ENQUIRY - $subject"
        val newMessage = "$email\n\n$message"
        emailService.sendSimpleMessage(infoEmailAddress, infoEmailAddress, emailSubject, newMessage)
    }

}