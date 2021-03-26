package com.laces.core.utils

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import org.apache.commons.lang3.StringUtils.*
import java.lang.reflect.Field
import java.util.*

fun getFieldValueForObject(field: Field, obj : Any): String {
    val javaClass = obj.javaClass
    try {
        val fieldValue = retrieveValueThroughGetter(javaClass, field, obj)

        return fieldValue?.toString() ?: ""
    } catch (e: Exception) {
        println("Unable to retrieve display: $javaClass")
        e.printStackTrace()
    }
    return "PRISM ERROR: Unable to retrieve display"
}

fun retrieveValueThroughGetter(javaClass: Class<*>, field: Field, obj: Any): Any? {
    // Booleans in kotlin classes use "getBoolean" by default
    val isBoolean = if (javaClass.isKotlinClass()) {
        false
    } else {
        field.type == Boolean::class.javaPrimitiveType || field.type == Boolean::class.java || field.type == java.lang.Boolean::class.java
    }
    val preAppend = if (isBoolean) "is" else "get"
    val method = javaClass.getMethod(preAppend + capitalize(field.name))
    return method.invoke(obj)
}

// Only get fields relating to transformation rule. Anything above this should not be considered.
// Removes any with the annotation @IgnoreFieldRecording
fun allFieldsForClass(
        clazz: Class<*>,
        lastClazz: Class<*> = Object::getClass.javaClass
): List<Field> {
    val fields = ArrayList<Field>()
    var currentClazz = clazz
    while (lastClazz.isAssignableFrom(currentClazz)) {
        val validFields = currentClazz.declaredFields
                .filter { !it.isAnnotationPresent(IgnoreGetFieldForClass::class.java) }
        fields.addAll(validFields)

        if(currentClazz.superclass == null){
            break
        }

        currentClazz = currentClazz.superclass
    }
    return fields
}

fun classNameWithSpaces(clazz: Class<*>): String {
    val splitName = splitByCharacterTypeCamelCase(clazz.simpleName)
    val className = join(splitName, " ")
    return defaultIfBlank(className, "Name not available")
}