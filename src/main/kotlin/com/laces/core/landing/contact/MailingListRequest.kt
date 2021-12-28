package com.laces.core.landing.contact

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.form.core.FormAnnotations.FormData

@JsonSchemaTitle("")
@FormData(name = "Email Submission Form", isPublic = true, groups = [CONTACT])
class EmailSubmissionForm(
        @field:JsonSchemaTitle("Email") val email: String
)

class MailingListRequestDto(
        val formData: EmailSubmissionForm,
        val extraInfo: Map<String, Any?> = emptyMap()
)