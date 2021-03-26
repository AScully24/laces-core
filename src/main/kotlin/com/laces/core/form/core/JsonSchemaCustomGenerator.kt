package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JsonSchemaCustomGenerator(
    private val jsonSchemaGenerator: JsonSchemaGenerator
) {

    @Autowired(required = false)
    var suppliers: List<JsonInjectionSupplier>? = null

    fun constructModifiedSchema(clazz: Class<*>): JsonNode {
        val originalSchema: JsonNode = jsonSchemaGenerator.generateJsonSchema(clazz)
        val updatedSchema: JsonNode = removeSchemaVersion(originalSchema)

        // JsonSchemaTitle doesn't seem to be working with this Mkbor library when applied to the parent schema object
        // Works fine with embedded classes.
        // I'm dealing with it myself here. Consider this tech debt that should be review if necessary.
        val title = getSchemaTitle(clazz)
        return addKeyValueToNode(updatedSchema, title, "title")
    }

    private fun removeSchemaVersion(jsonSchema: JsonNode): JsonNode {
        (jsonSchema as ObjectNode).remove("\$schema")
        return jsonSchema
    }

    private fun addKeyValueToNode(jsonSchema: JsonNode, value: String, key: String): JsonNode {
        (jsonSchema as ObjectNode).put(key, value)
        return jsonSchema
    }

    private fun overrideRequired(jsonSchema: JsonNode): JsonNode {
        val get = jsonSchema.get("definitions").get("HeaderKeyValueSecurityConfigurationDto")
        (get as ObjectNode).replace("required", generateStringArrayNode(emptyList()))
        return jsonSchema
    }
}