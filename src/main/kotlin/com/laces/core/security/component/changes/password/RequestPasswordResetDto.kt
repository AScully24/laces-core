package com.laces.core.security.component.changes.password

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.form.core.FormAnnotations.FormData

@FormData(name = "RequestPasswordReset", isPublic = true)
@JsonSchemaTitle("Request Password Change")
data class RequestPasswordResetDto(

    @JsonProperty(value = "Email")
    val email: String,
)