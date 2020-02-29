package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import scala.Tuple2
import scala.collection.immutable.Map
import java.util.function.Supplier

@Component
class SchemaConfig {

    @Autowired(required = false)
    var suppliers: List<JsonInjectionSupplier>? = null

    @Bean
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
        val jsonSchemaGenerator = JsonSchemaGenerator(objectMapper, config)
        return jsonSchemaGenerator
    }

}