package com.laces.core.form.core

import com.laces.core.form.core.FormAnnotations.FormData
import com.laces.core.form.dto.FlowResponse
import com.laces.core.form.dto.FlowStepResponse
import com.laces.core.responses.FormAnnotationNotPresent
import org.springframework.stereotype.Component

@Component
class FormRepository(
        private val jsonSchemaCustomGenerator: JsonSchemaCustomGenerator,
        private val flows: List<Flow>,
        private val packageLocations: PackageLocations
) {

    fun findFlow(flowName: String): FlowResponse? {
        return flows.firstOrNull { it.flowName == flowName }
                ?.let { flow ->
                    val flowStepResponses = flow.steps
                            .map { flowStep -> createFlowStepResponse(flowStep, findForms { formStepFilter(it, flowStep) }) }
                    FlowResponse(flow.title,flow.submissionUrl, flowStepResponses)
                }
    }

    fun getFlowNames(): List<String> {
        return flows.map { it.flowName }
    }

    fun getNames(): Set<String> {
        val names = mutableSetOf<String>()
        getFormClasses { names.add(it.name) }
        return names.toSet()
    }

    fun getGroups(): Set<String> {
        val groups = mutableSetOf<String>()
        getFormClasses { groups.addAll(it.groups) }
        return groups.toSet()
    }

    fun findForms(filter: (FormData) -> Boolean): List<Form> {
        val formClasses = getFormClasses(filter)
        return formClasses.map { createFormMetaData(it) }
    }

    private fun getFormClasses(filter: (FormData) -> Boolean): List<Class<*>> {
        return ClassLister(packageLocations.packages)
                .allClassesWithAnnotation(FormData::class.java, filter)
    }

    private fun createFlowStepResponse(flowStep: FlowStep, metaData: List<Form>): FlowStepResponse {
        val filteredMetaData = metaData
                .filter { formMetaData -> isInFlow(formMetaData, flowStep) }

        val fieldName = if (filteredMetaData.size == 1 && filteredMetaData[0].name.isNotBlank()) {
            filteredMetaData[0].name
        } else flowStep.group ?: "NOT_SET"

        return FlowStepResponse(filteredMetaData, flowStep.title, fieldName, flowStep.asArray)
    }

    private fun isInFlow(form: Form, flowStep: FlowStep): Boolean {
        val group = flowStep.group
        val formName = flowStep.formName

        return form.groups.contains(group) || form.name == formName
    }

    private fun formStepFilter(formData: FormData, flowStep: FlowStep) =
            formData.name == flowStep.formName || formData.groups.contains(flowStep.group)

    private fun createFormMetaData(clazz: Class<*>): Form {
        val formAnnotation = clazz.getAnnotation(FormData::class.java) ?: throw FormAnnotationNotPresent()
        val title = getSchemaTitle(clazz)
        val name = getSchemaName(clazz)
        val modifiedSchema = jsonSchemaCustomGenerator.constructModifiedSchema(clazz)
        val groups = formAnnotation.groups.asList()

        return Form(name, title, clazz.canonicalName, modifiedSchema, groups)
    }
}
