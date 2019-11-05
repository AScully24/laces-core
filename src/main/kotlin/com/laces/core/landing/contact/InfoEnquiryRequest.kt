package com.laces.core.landing.contact

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.Form

@Form(groups = [CONTACT], isPublic = true)
@JsonSchemaTitle("General Enquiry")
data class InfoEnquiryRequest(
        @field:JsonProperty(value = "First Name", required = true)
        val firstName: String,

        @field:JsonProperty(value = "Second Name", required = true)
        val secondName: String,

        @field:JsonProperty(value = "Email", required = true)
        val userEmail: String,

        @field:JsonProperty(value = "Subject", required = true)
        val userSubject: String,

        @field:JsonProperty(value = "Enquiry", required = true)
        val message: String
)