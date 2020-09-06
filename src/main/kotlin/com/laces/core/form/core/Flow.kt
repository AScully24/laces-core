package com.laces.core.form.core

class Flow(
        val flowName: String,
        val title: String,
        val submissionUrl: String,
        val steps: List<FlowStep>
)

data class FlowStep(
        val formName: String?,
        val group: String?,
        val title: String,
        val asArray: Boolean = false,
        val required: Boolean = true,
        val fieldName: String = formName ?: group ?: "NOT_SET"
)