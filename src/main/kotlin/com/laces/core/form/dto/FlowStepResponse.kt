package com.laces.core.form.dto

import com.laces.core.form.core.Form

data class FlowResponse(
        val title: String,
        val submissionUrl: String,
        val flowSteps : List<FlowStepResponse>
)

data class FlowStepResponse(
        val formMetaData: List<Form>,
        val title: String,
        val fieldName: String,
        val asArray: Boolean
)