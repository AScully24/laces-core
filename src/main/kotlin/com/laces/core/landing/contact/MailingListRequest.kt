package com.laces.core.landing.contact

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData

@JsonSchemaTitle("")
@FormData(name = "Mailing List", isPublic = true, groups = [CONTACT])
class MailingListRequest(@field:JsonSchemaTitle("Email") val email: String)