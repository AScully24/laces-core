package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

fun generateNodeWithStringArrayProperty(propertyName: String, values: List<String>): ObjectNode {
    val schema = ObjectMapper().createObjectNode()
    val arrayNode = ObjectMapper().createArrayNode()
    values.forEach { arrayNode.add(it) }
    schema.set<ArrayNode>(propertyName, arrayNode)

    return schema
}

fun generateStringArrayNode(values: List<String>): ArrayNode {
    val arrayNode = ObjectMapper().createArrayNode()
    values.forEach { arrayNode.add(it) }
    return arrayNode
}

fun generateArrayNode(values: List<JsonNode>): ArrayNode {
    val arrayNode = ObjectMapper().createArrayNode()
    arrayNode.addAll(values)
    return arrayNode
}


fun generateNodeWithValues(nodeName: String, values: Map<String, JsonNode>): JsonNode {
    val child = json(values)
    return ObjectMapper()
            .createObjectNode()
            .also { it.set<JsonNode>(nodeName, child) }

}

fun json(values: Map<String, JsonNode>): JsonNode {
    return ObjectMapper()
            .createObjectNode()
            .also { it.setAll<JsonNode>(values) }
}

operator fun MutableMap<String, JsonNode>.set(key: String, value: String) {
    this[key] = TextNode(value)
}

operator fun MutableMap<String, JsonNode>.set(key: String, values: List<String>) {
    this[key] = generateStringArrayNode(values)
}

fun json(init: MutableMap<String, JsonNode>.() -> Unit): JsonNode {

    val values = mutableMapOf<String, JsonNode>()
    values.init()
    return ObjectMapper()
            .createObjectNode()
            .also { it.setAll<JsonNode>(values) }

}

fun json(nodeName: String, value: JsonNode): JsonNode {
    return ObjectMapper().createObjectNode()
            .apply { set<JsonNode>(nodeName, value) }
}

fun generateTextNode(nodeName: String, value: String): JsonNode {
    return ObjectMapper().createObjectNode()
            .apply { set<TextNode>(nodeName, TextNode(value)) }
}

fun node(init: JsonNode.() -> Unit): JsonNode {
    val node = ObjectMapper().createObjectNode()
    node.init()
    return node
}