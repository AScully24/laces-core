package com.laces.core.landing.contact

import com.laces.core.email.isValidEmail
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/contact")
internal class ContactController(
        private val contactService: ContactService,
        private val mailingList: MailingListService? = null
) {

    @PostMapping("/info")
    fun submitGeneralEnquiry(@RequestBody infoEnquiryRequestForm: InfoEnquiryRequestDto) {
        val (name, email, message) = infoEnquiryRequestForm.formData
        val subject = infoEnquiryRequestForm.subject
        contactService.sendUserEnquiry(name ?: "Anonymous", email, subject, message)
    }

    @PostMapping("/email")
    fun signUpToMailingList(@RequestBody mailingListRequest: MailingListRequestDto) {
        isValidEmail(mailingListRequest.formData.email)
        mailingList?.sendMail(mailingListRequest.formData.email, mailingListRequest.extraInfo)
    }
}