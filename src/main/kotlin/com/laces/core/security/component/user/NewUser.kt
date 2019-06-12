package com.laces.core.security.component.user

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat
import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.core.FormAnnotations.FormType.REGISTER

@Form(settingsType = REGISTER, isPublic = true)
class NewUser {
    var username= ""

    @JsonSchemaFormat("password")
    var password = ""

    @JsonSchemaFormat("password")
    var confirmPassword = ""

    var additionalInfo : AdditionalInfo? = null
}