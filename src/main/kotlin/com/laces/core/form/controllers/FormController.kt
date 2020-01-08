package com.laces.core.form.controllers

import com.laces.core.form.dto.FlowResponse
import com.laces.core.form.dto.FormMetaDataResponse
import com.laces.core.form.dto.SettingsMetaDataDTOService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/form")
internal class FormController(
        val dtoSettingsMetaDataService: SettingsMetaDataDTOService
) {

    @GetMapping("settings")
    fun settingsSchemas(@RequestParam(required = false) formType: String?): List<FormMetaDataResponse> {
        if (formType == null) {
            return dtoSettingsMetaDataService.getMetaData()
        }
        return dtoSettingsMetaDataService.getMetaData(formType)
    }

    @GetMapping("public/flows")
    fun flows(@RequestParam flowName: String): FlowResponse {
        return dtoSettingsMetaDataService.getFlow(flowName)
    }

    @GetMapping("public")
    fun publicForms(@RequestParam(required = false) formType: String?): List<FormMetaDataResponse> {
        if (formType != null) {
            dtoSettingsMetaDataService.findAllPublicSettings(formType)
        }
        return dtoSettingsMetaDataService.findAllPublicSettings()
    }

    @GetMapping("public/single")
    fun publicFormByName(@RequestParam name: String): FormMetaDataResponse {
        return dtoSettingsMetaDataService.findPublicFormByName(name)
    }
}