package com.laces.core.security.component.changes.password

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData

@FormData(name = "RequestPasswordChange", isPublic = true)
@JsonSchemaTitle("Change Password")
data class PasswordChangeDto(

    @JsonProperty(value = "New Password")
    val newPassword: String,

    @JsonProperty(value = "Confirm New Password")
    val confirmNewPassword: String,

    @field:JsonProperty(value = "Current Password")
    val currentPassword: String,
)