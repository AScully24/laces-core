package com.laces.core.security.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData

@FormData(name = "RequestEmailChange", isPublic = true)
@JsonSchemaTitle("Request Email Change")
data class RequestEmailChangeDto(
    @JsonProperty(value = "New Email")
    val newEmail: String,

    @field:JsonProperty(value = "Password")
    val password: String,
)