package com.laces.core.form.core

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.FormAnnotations.Form
import com.laces.core.form.core.FormAnnotations.FormType

class FormMetaData<K> internal constructor(clazz: Class<out K>, val formType: FormType, val public : Boolean = false) {

    var name = "Placeholder"
    var fullClassPath: String? = null
    var jsonSchema: JsonNode? = null
    var formatType: String? = null

    init {
        construct(clazz)
    }

    private fun construct(clazz: Class<out K>) {
        this.fullClassPath = clazz.canonicalName
        val annotation = clazz.getAnnotation(Form::class.java)
        this.formatType = annotation?.formatType ?: ""
    }
}