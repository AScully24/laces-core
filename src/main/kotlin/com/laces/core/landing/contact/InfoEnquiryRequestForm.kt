package com.laces.core.landing.contact

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData
import com.laces.core.form.core.generateNodeArray
import com.laces.core.landing.contact.InfoEnquirySubject.MAILING_LIST
import java.util.function.Supplier

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

class InfoEnquirySubjectSupplier : Supplier<JsonNode> {
    override fun get(): JsonNode {
        return generateNodeArray("enumNames", InfoEnquirySubject.values().map { it.display })
    }
}