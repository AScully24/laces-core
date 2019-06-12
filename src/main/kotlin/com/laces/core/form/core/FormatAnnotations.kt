package com.laces.core.form.core

class FormatAnnotations {
    enum class FormatType(val formatName:String) {
        CSV("csv"), JSON("json"), XML("xml")
    }

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
    annotation class Format(val formatType: FormatType)

}