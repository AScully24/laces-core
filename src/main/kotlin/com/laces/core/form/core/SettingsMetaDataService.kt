package com.laces.core.form.core

import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.core.steps.FlowStep
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
@ConfigurationProperties(prefix = "laces.form")
class SettingsMetaDataService(
        private val jsonSchemaCustomGenerator: JsonSchemaCustomGenerator
) {

    var packages: MutableList<String> = mutableListOf()

    private val settingsMetaData = ArrayList<FormMetaData>()

    @PostConstruct
    fun init() {
        packages.add("com.laces")
        val classLister = ClassLister()
        val classes = packages.flatMap { classLister.listAllClassesInPackage(it) }

        val settingsClasses = classes
                .filter { it.isAnnotationPresent(Form::class.java) }

        settingsMetaData.addAll(populateData(settingsClasses))

        LOGGER.info("Number of forms: " + settingsMetaData.count())
    }

    private fun populateData(classes: List<Class<*>>): List<FormMetaData> {
        return classes.map {
            val formAnnotation = it.getAnnotation(Form::class.java)
            val title = getSchemaTitle(it)
            val name = getSchemaName(it)
            val modifiedSchema = jsonSchemaCustomGenerator.constructModifiedSchema(it)
            val flowSteps = formAnnotation.flow.map { flow -> FlowStep(flow.name, flow.stepNumber) }
            FormMetaData(
                    name,
                    title,
                    it.canonicalName,
                    modifiedSchema,
                    formAnnotation.isPublic,
                    formAnnotation.groups.asList(),
                    flowSteps
            )
        }
    }

    fun getSettingsMetaData(): List<FormMetaData> {
        return settingsMetaData
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsMetaDataService::class.java)
    }

}
