package com.laces.core.security.component.changes.password

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.form.core.FormAnnotations.FormData

data class ConfirmPasswordResetDto(

    @JsonProperty(value = "New Password")
    override val newPassword: String,

    @JsonProperty(value = "Confirm New Password")
    override val confirmNewPassword: String,

    val token: String,
) : ConfirmPasswordResetForm(newPassword, confirmNewPassword)

@FormData(name = "ConfirmPasswordReset", isPublic = true)
@JsonSchemaTitle("Confirm Password Change")
open class ConfirmPasswordResetForm(

    @JsonProperty(value = "New Password")
    open val newPassword: String,

    @JsonProperty(value = "Confirm New Password")
    open val confirmNewPassword: String
)