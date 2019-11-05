package com.laces.core.form.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.dto.MetaDataDTO
import com.laces.core.form.dto.SettingsMetaDataDTOService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/form")
internal class FormController(
        val dtoSettingsMetaDataService: SettingsMetaDataDTOService
) {

    @GetMapping("settings")
    fun settingsSchemas(@RequestParam(required = false) formType: String?): List<MetaDataDTO> {
        if (formType == null) {
            return dtoSettingsMetaDataService.getMetaData()
        }
        return dtoSettingsMetaDataService.getMetaData(formType)
    }

    @GetMapping("public")
    fun publicForms(@RequestParam(required = false) formType: String?): List<MetaDataDTO> {
        if (formType != null) {
            dtoSettingsMetaDataService.findAllPublicSettings(formType)
        }
        return dtoSettingsMetaDataService.findAllPublicSettings()
    }

    @GetMapping("public/single")
    fun publicFormByName(@RequestParam name: String): MetaDataDTO {
        return dtoSettingsMetaDataService.findPublicFormByName(name)
    }

    // Regex added to the end of GetMapping display to deal with periods in the class path.
    @GetMapping("{type:.+}")
    fun schemaByType(@PathVariable("type") type: String): JsonNode? {
        return dtoSettingsMetaDataService.findSchemaForClass(type)
    }
}