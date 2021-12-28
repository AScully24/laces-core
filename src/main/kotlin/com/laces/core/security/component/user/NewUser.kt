package com.laces.core.security.component.user

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat
import com.laces.core.security.component.user.NewUser.Companion.REGISTER
import com.laces.form.core.FormAnnotations.FormData

@FormData(groups = [REGISTER], isPublic = true)
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