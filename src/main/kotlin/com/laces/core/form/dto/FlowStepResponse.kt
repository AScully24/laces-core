package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode

data class FlowResponse(
        val flowSteps : List<FlowStepResponse>
)

data class FlowStepResponse(
        val stepNumber: Int,
        val jsonSchema: JsonNode
)