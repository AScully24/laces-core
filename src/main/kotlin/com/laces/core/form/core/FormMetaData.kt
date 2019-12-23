package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.steps.FlowStep

class FormMetaData internal constructor(
        val name: String,
        val title: String?,
        val fullClassPath: String,
        val jsonSchema: JsonNode,
        val public: Boolean = false,
        val groups: List<String> = emptyList(),
        val flowSteps: List<FlowStep> = emptyList()
)