package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode

class FormMetaData internal constructor(
        val name: String,
        val fullClassPath: String,
        val jsonSchema: JsonNode,
        val public: Boolean = false,
        val groups: List<String> = emptyList()
)