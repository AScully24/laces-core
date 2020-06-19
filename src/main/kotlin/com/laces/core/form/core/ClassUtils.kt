package com.laces.core.form.core

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData
import org.apache.commons.lang3.StringUtils.*

fun getClassAsReadableName(clazz: Class<*>): String {
    val splitClassName = splitByCharacterTypeCamelCase(clazz.simpleName)
    return join(splitClassName, " ")
}

fun getSchemaTitle(clazz: Class<*>): String {
    val schemaTitle = clazz.getAnnotation(JsonSchemaTitle::class.java)
    return schemaTitle?.value ?: getClassAsReadableName(clazz)
}

fun getSchemaName(clazz: Class<*>): String {
    val schemaTitle = clazz.getAnnotation(FormData::class.java)
    return schemaTitle?.name ?: uncapitalize(clazz.simpleName)
}