package com.laces.core.security.component.user

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat
import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.security.component.user.NewUser.Companion.REGISTER

@Form(groups = [REGISTER], isPublic = true)
class NewUser {
    var username= ""

    @field:JsonSchemaFormat("password")
    var password = ""

    @field:JsonSchemaFormat("password")
    var confirmPassword = ""

    var additionalInfo : AdditionalInfo? = null

    companion object {
        const val REGISTER = "REGISTER"
    }
}