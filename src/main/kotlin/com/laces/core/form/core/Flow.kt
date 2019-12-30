package com.laces.core.form.core

class Flow (
        val flowName: String,
        val title: String,
        val steps : List<FlowStep>
)

class FlowStep(
        val formName: String?,
        val group: String?
)