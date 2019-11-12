package com.laces.core.form.core

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import org.apache.commons.lang3.StringUtils.join
import org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase

fun getClassAsReadableName(clazz: Class<*>): String {
    val splitClassName = splitByCharacterTypeCamelCase(clazz.simpleName)
    return join(splitClassName, " ")
}

fun getSchemaTitle(clazz: Class<*>): String {
    val schemaTitle = clazz.getAnnotation(JsonSchemaTitle::class.java)
    return schemaTitle?.value ?: getClassAsReadableName(clazz)
}

fun getSchemaName(clazz: Class<*>): String {
    val schemaTitle = clazz.getAnnotation(FormAnnotations.Form::class.java)
    return schemaTitle?.name ?: getSchemaTitle(clazz).ifBlank { getClassAsReadableName(clazz) }
}