package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.stereotype.Service

@Service
class JsonSchemaCustomGenerator {

    fun constructModifiedSchema(clazz: Class<*>): JsonNode {
        val originalSchema: JsonNode = removeSchemaVersion(createSchema(clazz))
        // JsonSchemaTitle doesn't seem to be working with this Mkbor library when applied to the parent schema object
        // Works fine with embedded classes.
        // I'm dealing with it myself here. Consider this tech debt that should be review if necessary.
        val title = getSchemaTitle(clazz)
        return addKeyValueToNode(originalSchema, title, "title")
    }

    private fun createSchema(clazz: Class<*>): JsonNode {

        val objectMapper = ObjectMapper()
        val config = JsonSchemaConfig.vanillaJsonSchemaDraft4()
        val jsonSchemaGenerator = JsonSchemaGenerator(objectMapper, config)

        return jsonSchemaGenerator.generateJsonSchema(clazz)
    }


    private fun removeSchemaVersion(jsonSchema: JsonNode): JsonNode {
        (jsonSchema as ObjectNode).remove("\$schema")
        return jsonSchema
    }

    private fun addKeyValueToNode(jsonSchema: JsonNode, value: String, key: String): JsonNode {
        (jsonSchema as ObjectNode).put(key, value)
        return jsonSchema
    }
}