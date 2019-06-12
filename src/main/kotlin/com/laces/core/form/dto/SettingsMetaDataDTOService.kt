package com.laces.core.form.dto

import com.laces.core.form.core.SettingsMetaDataService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class SettingsMetaDataDTOService(private val settingsMetaDataService: SettingsMetaDataService) {

    private val mapper = ModelMapper()

    fun findAllSettings(): List<SettingsMetaDataDTO> {
        return settingsMetaDataService.getSettingsMetaData()
                .map { mapper.map(it, SettingsMetaDataDTO::class.java) }
    }
}
