package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.Tuple2
import scala.collection.immutable.Map
import java.util.function.Supplier

@Service
class JsonSchemaCustomGenerator {

    @Autowired(required = false)
    var suppliers: List<JsonInjectionSupplier>? = null

    fun constructModifiedSchema(clazz: Class<*>): JsonNode {
        val originalSchema: JsonNode = removeSchemaVersion(createSchema(clazz))
        // JsonSchemaTitle doesn't seem to be working with this Mkbor library when applied to the parent schema object
        // Works fine with embedded classes.
        // I'm dealing with it myself here. Consider this tech debt that should be review if necessary.
        val title = getSchemaTitle(clazz)
        return addKeyValueToNode(originalSchema, title, "title")
    }

    private fun createSchema(clazz: Class<*>): JsonNode {

        val jsonSchemaGenerator = createJsonSchemaGenerator()

        return jsonSchemaGenerator.generateJsonSchema(clazz)
    }

    fun createJsonSchemaGenerator(): JsonSchemaGenerator {
        val objectMapper = ObjectMapper()
        val config = JsonSchemaConfig.vanillaJsonSchemaDraft4().run {
            val baseJsonSuppliers: Map<String, Supplier<JsonNode>> = jsonSuppliers()

            val finalJsonSuppliers = suppliers?.let {
                it.fold(baseJsonSuppliers, { acc, jsonSuppler ->
                    acc.`$plus`(Tuple2(jsonSuppler.lookupKey, jsonSuppler as Supplier<JsonNode>))
                })
            } ?: baseJsonSuppliers

            JsonSchemaConfig(
                    autoGenerateTitleForProperties(),
                    defaultArrayFormat(),
                    useOneOfForOption(),
                    useOneOfForNullables(),
                    usePropertyOrdering(),
                    hidePolymorphismTypeProperty(),
                    disableWarnings(),
                    useMinLengthForNotNull(),
                    useTypeIdForDefinitionName(),
                    customType2FormatMapping(),
                    useMultipleEditorSelectViaProperty(),
                    uniqueItemClasses(),
                    classTypeReMapping(),
                    finalJsonSuppliers,
                    subclassesResolver(),
                    failOnUnknownProperties()
            )
        }
        return JsonSchemaGenerator(objectMapper, config)
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