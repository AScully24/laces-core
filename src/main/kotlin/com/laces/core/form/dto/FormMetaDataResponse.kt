package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode

class FormMetaDataResponse {
    var className: String? = null
    var fullClassPath: String? = null
    var jsonSchema: JsonNode = NullNode.instance
    var name: String? = null
    var title: String? = null
    var public: Boolean = false
    var groups: MutableList<String> = mutableListOf()
}