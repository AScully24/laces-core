package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class JsonSchemaPostProcessor {

    companion object{
        private val LOG = getLogger(JsonSchemaPostProcessor::class.java)
    }

    fun process(clazz: Class<*>, schema: JsonNode): JsonNode{
        val definitionRequiredConfig = clazz.getAnnotation(JsonSchemaRequired::class.java)?.definitions ?: return schema
        val definitions = schema.get("definitions")

        definitionRequiredConfig
            .filter { definitions.get(it.definition) == null }
            .forEach { LOG.warn("Unable to find definition for in schema: ${it.definition}") }

        definitionRequiredConfig
            .mapNotNull {
                val jsonNode = definitions.get(it.definition) ?: return@mapNotNull null
                jsonNode to it.fieldNames
            }
            .forEach { (jsonNode, fieldNames) ->
                (jsonNode as ObjectNode).set<ArrayNode>("required", generateStringArrayNode(fieldNames.toList()))
            }
        return schema
    }
}