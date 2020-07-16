package com.laces.core.landing.contact

interface MailingListService {
    fun sendMail(email: String, extraInfo: Map<String, Any?>)
}