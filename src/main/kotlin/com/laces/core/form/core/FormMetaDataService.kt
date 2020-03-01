package com.laces.core.form.core

import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.dto.FlowResponse
import com.laces.core.form.dto.FlowStepResponse
import com.laces.core.responses.FormAnnotationNotPresent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class FormMetaDataService(
        private val jsonSchemaCustomGenerator: JsonSchemaCustomGenerator,
        private val flows: List<Flow>,
        private val packageLocations : PackageLocations
) {

    lateinit var staticMetaData: List<FormMetaData>

    @PostConstruct
    fun init(){
        staticMetaData = findForms { !it.isDynamic }
    }

    fun findAllForms(): List<FormMetaData> {
        return listOf(findForms { it.isDynamic }, staticMetaData).flatten()
    }

    private fun findForms(filter: (Form) -> Boolean): List<FormMetaData> {
        val classLister = ClassLister(packageLocations.packages)
        val settingsClasses = classLister.allClassesWithAnnotation(Form::class.java, filter)
        return settingsClasses.map { createFormMetaData(it) }
    }

    private fun createFlowStepResponse(flowStep: FlowStep, metaData: List<FormMetaData>): FlowStepResponse {
        val filteredMetaData = metaData
                .filter { formMetaData -> isInFlow(formMetaData, flowStep) }

        val fieldName = if (filteredMetaData.size == 1 && filteredMetaData[0].name.isNotBlank()){
            filteredMetaData[0].name
        } else flowStep.group ?: "NOT_SET"

        return FlowStepResponse(filteredMetaData, flowStep.title, fieldName, flowStep.asArray)
    }

    private fun isInFlow(formMetaData: FormMetaData, flowStep: FlowStep): Boolean {
        val group = flowStep.group
        val formName = flowStep.formName

        return formMetaData.groups.contains(group) || formMetaData.name == formName
    }

    fun getFlow(flowName: String): FlowResponse? {

        val allFormMetaData = findAllForms()
        val toMap = flows.map { flow ->
            val flowResponses = flow.steps
                    .map { flowStep -> createFlowStepResponse(flowStep, allFormMetaData) }
            flow.flowName to FlowResponse(flow.title, flow.submissionUrl, flowResponses)
        }.toMap()
        return toMap[flowName]
    }

    private fun createFormMetaData(clazz: Class<*>): FormMetaData {
        val formAnnotation = clazz.getAnnotation(Form::class.java) ?: throw FormAnnotationNotPresent()
        val title = getSchemaTitle(clazz)
        val name = getSchemaName(clazz)
        val modifiedSchema = jsonSchemaCustomGenerator.constructModifiedSchema(clazz)
        val groups = formAnnotation.groups.asList()

        return FormMetaData(
                name,
                title,
                clazz.canonicalName,
                modifiedSchema,
                formAnnotation.isPublic,
                groups
        )
    }

    private fun isInFlow(groups: List<String>, flow: Flow, formName: String) =
            groups.any { flow.steps.any { step -> step.group == it } } || flow.steps.any { it.formName == formName }

}
