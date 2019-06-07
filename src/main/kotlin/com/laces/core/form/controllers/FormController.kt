package com.laces.core.form.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.FormAnnotations.FormType
import com.laces.core.form.core.SettingsMetaDataService
import com.laces.core.form.dto.SettingsMetaDataDTO
import com.laces.core.form.dto.SettingsMetaDataDTOService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/form")
internal class FormController(
        val dtoSettingsMetaDataService: SettingsMetaDataDTOService,
        val settingsMetaDataService: SettingsMetaDataService

) {

    @GetMapping("settings")
    fun settingsSchemas(@RequestParam(required = false) formType: FormType?): List<SettingsMetaDataDTO> {
        return dtoSettingsMetaDataService.findAllSettings()
                .filter { formType == null || it.formType == formType }
    }

    @GetMapping("public")
    fun publicForms(@RequestParam(required = false) formType: FormType?): List<SettingsMetaDataDTO> {
        return dtoSettingsMetaDataService.findAllSettings()
                .filter { it.public && (formType == null || it.formType == formType )}
    }

    // Regex added to the end of GetMapping display to deal with periods in the class path.
    @GetMapping("{type:.+}")
    fun schemaByType(@PathVariable("type") type: String): JsonNode? {
        return settingsMetaDataService.getJsonSchemaForClass(type)
    }
}