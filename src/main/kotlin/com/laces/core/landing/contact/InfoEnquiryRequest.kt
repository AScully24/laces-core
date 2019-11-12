package com.laces.core.landing.contact

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.core.generateNodeArray
import com.laces.core.landing.contact.InfoEnquirySubject.MAILING_LIST
import java.util.function.Supplier

@Form(groups = [CONTACT], isPublic = true, name="General Enquiry")
@JsonSchemaTitle("")
data class InfoEnquiryRequest(
        @field:JsonProperty(value = "First Name", required = true)
        val firstName: String,

        @field:JsonProperty(value = "Second Name", required = true)
        val secondName: String,

        @field:JsonProperty(value = "Email", required = true)
        val userEmail: String,

        @field:JsonSchemaInject(jsonSupplier = InfoEnquirySubjectSupplier::class)
        @field:JsonProperty(value = "Subject", required = true)
        val userSubject: InfoEnquirySubject = MAILING_LIST,

        @field:JsonProperty(value = "Enquiry", required = true)
        val message: String
)

class InfoEnquirySubjectSupplier : Supplier<JsonNode> {
    override fun get(): JsonNode {
        return generateNodeArray("enumNames", InfoEnquirySubject.values().map { it.display })
    }
}