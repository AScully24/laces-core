package com.laces.core.form.core

import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.core.steps.FlowStep
import com.laces.core.form.dto.FlowStepResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
@ConfigurationProperties(prefix = "laces.form")
class SettingsMetaDataService(
        private val jsonSchemaCustomGenerator: JsonSchemaCustomGenerator,
        private val flows: List<Flow>,
        var packages: MutableList<String> = mutableListOf()
) {

    lateinit var settingsMetaData: List<FormMetaData>
    lateinit var flowMap: Map<String, List<FlowStepResponse>>

    @PostConstruct
    fun init() {
        val settingsClasses = listClassesMatching(Form::class.java)
        settingsMetaData = settingsClasses.map { createFormMetaData(it, flows) }

        flowMap = flows.map {

            val flowResponses = it.steps
                    .mapIndexed { index, flowStep ->
                        val matchingMetaData = settingsMetaData
                                .filter { formMetaData -> formMetaData.groups.contains(flowStep.group) || formMetaData.name == flowStep.formName }
                                .map { formMetaData -> formMetaData.jsonSchema }
                        FlowStepResponse(index, it.title, matchingMetaData)
                    }
            it.flowName to flowResponses
        }.toMap()

        LOGGER.info("Number of forms: " + settingsMetaData.count())
    }

    private fun listClassesMatching(clazz: Class<out Annotation>): List<Class<*>> {
        packages.add("com.laces")
        val classLister = ClassLister()
        return packages.flatMap { classLister.listAllClassesInPackage(it) }
                .filter { it.isAnnotationPresent(clazz) }

    }

    fun getFlow(flowName: String): List<FlowStepResponse> {
        return flowMap[flowName] ?: emptyList()
    }

    private fun createFormMetaData(clazz: Class<*>, flows: List<Flow>): FormMetaData {
        val formAnnotation = clazz.getAnnotation(Form::class.java)
        val title = getSchemaTitle(clazz)
        val name = getSchemaName(clazz)
        val modifiedSchema = jsonSchemaCustomGenerator.constructModifiedSchema(clazz)
        val groups = formAnnotation.groups.asList()

        val flowSteps = flows
                .filter { isInFlow(groups, it, name) }
                .map { it.steps.mapIndexed { index, _ -> FlowStep(it.flowName, index) } }
                .flatten()

        return FormMetaData(
                name,
                title,
                clazz.canonicalName,
                modifiedSchema,
                formAnnotation.isPublic,
                groups,
                flowSteps
        )
    }

    private fun isInFlow(groups: List<String>, flow: Flow, formName: String) =
            groups.any { flow.steps.any { step -> step.group == it } } || flow.steps.any { it.formName == formName }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsMetaDataService::class.java)
    }

}
