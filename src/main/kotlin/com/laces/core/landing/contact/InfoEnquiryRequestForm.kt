package com.laces.core.landing.contact

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.landing.contact.InfoEnquirySubject.MAILING_LIST
import com.laces.form.core.FormAnnotations.FormData

data class InfoEnquiryRequestDto(
        val formData: InfoEnquiryRequestForm,
        val subject: InfoEnquirySubject = MAILING_LIST
)


@FormData(groups = [CONTACT], isPublic = true, name = "General Enquiry")
@JsonSchemaTitle("")
data class InfoEnquiryRequestForm(
        @field:JsonProperty(value = "Email", required = true)
        val email: String,

        @field:JsonProperty(value = "Enquiry", required = true)
        val message: String
)