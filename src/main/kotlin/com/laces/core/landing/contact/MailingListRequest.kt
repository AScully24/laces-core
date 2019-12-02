package com.laces.core.landing.contact

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.Form

@JsonSchemaTitle("")
@Form(name = "Mailing List", isPublic = true, groups = [CONTACT])
class MailingListRequest(@field:JsonSchemaTitle("Email") val email: String)