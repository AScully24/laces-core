package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.Form
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
@ConfigurationProperties(prefix = "laces.form")
class SettingsMetaDataService {

    var packages: MutableList<String> = mutableListOf()

    private val settingsMetaData = ArrayList<FormMetaData<*>>()

    @PostConstruct
    fun init () {
        packages.add("com.laces")
        val classLister = ClassLister()
        val classes = packages.flatMap {classLister.listAllClassesInPackage(it) }

        val settingsClasses = classes
                .filter { it.isAnnotationPresent(Form::class.java) }

        settingsMetaData.addAll(populateData(settingsClasses))

        LOGGER.info("Number of forms: " + settingsMetaData.count())
    }

    private fun populateData(classes: List<Class<*>>): List<FormMetaData<*>> {
        val toReturn = ArrayList<FormMetaData<*>>()

        for (clazz in classes) {
            val settingsAnnotation = clazz.getAnnotation(Form::class.java)
            val newMetaData = FormMetaData(
                    clazz,
                    settingsAnnotation.settingsType,
                    settingsAnnotation.isPublic
            )
            newMetaData.jsonSchema = createSchema(clazz).orElse(null)

            val originalSchema = newMetaData.jsonSchema

            newMetaData.jsonSchema = removeSchemaVersion(originalSchema)

            // JsonSchemaTitle doesn't seem to be working with this Mkbor library when applied to the parent schema object
            // Works fine with embedded classes.
            // I'm dealing with it myself here. Consider this tech debt that should be review if necessary.
            val title = getSchemaTitle(clazz)
            val jsonNode = addKeyValueToNode(originalSchema, title, "title")

            newMetaData.jsonSchema = jsonNode
            newMetaData.name = title ?: "Placeholder"

            toReturn.add(newMetaData)
        }

        return toReturn
    }

    private fun getSchemaTitle(clazz: Class<*>): String? {
        val schemaTitle = clazz.getAnnotation(JsonSchemaTitle::class.java)
        return if (schemaTitle == null) {
            val splitClassName = StringUtils.splitByCharacterTypeCamelCase(clazz.simpleName)
            StringUtils.join(splitClassName, " ")
        } else {
            schemaTitle.value
        }
    }

    private fun removeSchemaVersion(jsonSchema: JsonNode?): JsonNode? {
        if (jsonSchema != null) {
            (jsonSchema as ObjectNode).remove("\$schema")
        }
        return jsonSchema
    }

    private fun addKeyValueToNode(jsonSchema: JsonNode?, value: String?, key: String): JsonNode? {
        if (jsonSchema != null) {
            (jsonSchema as ObjectNode).put(key, value)
        }
        return jsonSchema
    }

    private fun createSchema(clazz: Class<*>): Optional<JsonNode> {

        val objectMapper = ObjectMapper()
        val config = JsonSchemaConfig.vanillaJsonSchemaDraft4()
        val jsonSchemaGenerator = JsonSchemaGenerator(objectMapper, config)

        val jsonSchema = jsonSchemaGenerator.generateJsonSchema(clazz)
        return Optional.ofNullable(jsonSchema)
    }

    fun getJsonSchemaForClass(fullClassPath: String): JsonNode? {
        return settingsMetaData
                .first { it.fullClassPath.equals(fullClassPath, ignoreCase = true) }.jsonSchema
    }

    fun getSettingsMetaData(): List<FormMetaData<*>> {
        return settingsMetaData
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsMetaDataService::class.java)
    }

}
