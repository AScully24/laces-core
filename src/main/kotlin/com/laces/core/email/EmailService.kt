package com.laces.core.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component


@Component
class EmailService(
        val emailSender: JavaMailSender
) {
    fun sendSimpleMessage(to: String, subject: String, text: String) {

        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.from = "testing@something.com"
        message.text = text
        emailSender.send(message)

    }
}