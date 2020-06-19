package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode

class FormDto(
        val fullClassPath: String? = null,
        val jsonSchema: JsonNode = NullNode.instance,
        val name: String? = null,
        val title: String? = null,
        val groups: List<String> = emptyList()
)