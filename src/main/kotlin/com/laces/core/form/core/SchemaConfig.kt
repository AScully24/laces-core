package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import com.kjetland.jackson.jsonSchema.SubclassesResolverImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import scala.Tuple2
import scala.collection.immutable.Map
import java.util.function.Supplier

@Configuration
class SchemaConfig(
        private val packageLocations : PackageLocations
) {

    @Autowired(required = false)
    var suppliers: List<JsonInjectionSupplier>? = null

    @Bean
    fun createJsonSchemaGenerator(): JsonSchemaGenerator {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val resolver = SubclassesResolverImpl().withPackagesToScan(packageLocations.packages)
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
                    true,
                    false,
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
                    resolver,
                    failOnUnknownProperties()
            )
        }
        return JsonSchemaGenerator(objectMapper, config)
    }

}

@Configuration
@ConfigurationProperties("laces.form")
class Prepare {
    var packages: MutableList<String> = mutableListOf()

    @Bean
    fun schemaPackageLocations() = PackageLocations(listOf(packages, listOf("com.laces")).flatten())
}

data class PackageLocations(val packages: List<String>)