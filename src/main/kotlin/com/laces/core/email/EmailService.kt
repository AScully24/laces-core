package com.laces.core.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailService(
        val emailSender: JavaMailSender,
        @Value("\${laces.security.registration.from-email}")
        val fromEmail: String
) {
    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.from = fromEmail
        message.text = text
        emailSender.send(message)
    }
}