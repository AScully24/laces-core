package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.FormAnnotations

class SettingsMetaDataDTO {
    var className: String? = null
    var fullClassPath: String? = null
    var jsonSchema: JsonNode? = null
    var formType: FormAnnotations.FormType? = null
    var name: String? = null
    var public : Boolean = false
}
