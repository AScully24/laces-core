package com.laces.core.form.core

annotation class JsonSchemaRequired(val definitions: Array<RequiredField>)

annotation class RequiredField(val definition: String, val fieldNames: Array<String>)