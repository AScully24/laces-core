package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode

data class Form internal constructor(
        val name: String,
        val title: String?,
        val fullClassPath: String,
        val jsonSchema: JsonNode,
        val groups: List<String> = emptyList()
)