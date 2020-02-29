package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import java.util.function.Supplier

interface JsonInjectionSupplier : Supplier<JsonNode> {
    val lookupKey : String
}