package com.laces.core.form.dto

import com.laces.core.form.core.FormMetaData

data class FlowResponse(
        val title: String,
        val submissionUrl: String,
        val flowSteps : List<FlowStepResponse>
)

data class FlowStepResponse(
        val formMetaData: List<FormMetaData>,
        val title: String,
        val fieldName: String,
        val asArray: Boolean
)