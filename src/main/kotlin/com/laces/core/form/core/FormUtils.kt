package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

fun generateNodeArray(nodeName: String, values: List<String>): JsonNode {
    val schema = ObjectMapper().createObjectNode()
    val arrayNode = ObjectMapper().createArrayNode()
    values.forEach { arrayNode.add(it) }
    schema.set(nodeName, arrayNode)

    return schema
}