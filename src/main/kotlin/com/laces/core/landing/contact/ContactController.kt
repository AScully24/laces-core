package com.laces.core.landing.contact

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/contact")
internal class ContactController(
        private val contactService: ContactService
) {

    @PostMapping("/info")
    fun submitGeneralEnquiry(@RequestBody infoEnquiryRequest: InfoEnquiryRequest) {
        val (firstName, secondName, userEmail, userSubject, message) = infoEnquiryRequest
        contactService.sendUserEnquiry(firstName, secondName, userEmail, userSubject.display, message)
    }
}