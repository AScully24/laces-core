package com.laces.core.security.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.form.core.FormAnnotations.FormData

@FormData(name = "RequestEmailChange", isPublic = true)
@JsonSchemaTitle("Change Email")
data class RequestEmailChangeDto(
    @JsonProperty(value = "New Email")
    val newEmail: String,

    @JsonProperty(value = "Confirm New Email")
    val confirmNewEmail: String,

    @field:JsonProperty(value = "Password")
    val password: String,
)