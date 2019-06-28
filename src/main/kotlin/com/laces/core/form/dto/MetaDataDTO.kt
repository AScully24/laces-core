package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode

class MetaDataDTO {
    var className: String? = null
    var fullClassPath: String? = null
    var jsonSchema: JsonNode? = null
    var name: String? = null
    var public : Boolean = false
    var groups : MutableList<String> = mutableListOf()
}
