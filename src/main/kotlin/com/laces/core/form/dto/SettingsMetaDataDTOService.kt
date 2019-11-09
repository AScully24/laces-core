package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.SettingsMetaDataService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class SettingsMetaDataDTOService(
        settingsMetaDataService: SettingsMetaDataService
) {

    private val metaDataDtos: List<MetaDataDTO>

    init {
        val mapper = ModelMapper()
        metaDataDtos = settingsMetaDataService.getSettingsMetaData()
                .map { mapper.map(it, MetaDataDTO::class.java) }
    }

    fun getMetaData(): List<MetaDataDTO> {
        return metaDataDtos
    }

    fun getMetaData(group: String): List<MetaDataDTO> {
        return metaDataDtos.filter {
            it.groups.any { itGroup -> itGroup == group }
        }
    }

    fun getMetaDataContainingAll(vararg groups: String): List<MetaDataDTO> {
        return metaDataDtos.filter { it.groups.containsAll(groups.toList())}
    }

    fun findAllPublicSettings(): List<MetaDataDTO> {
        return metaDataDtos.filter { it.public }
    }

    fun findAllPublicSettings(group : String): List<MetaDataDTO> {
        return findAllPublicSettings().filter { it.groups.contains(group)}
    }

    fun findPublicFormByName(name : String): MetaDataDTO {
        return findAllPublicSettings().first { it.name == name }
    }

    fun findSchemaForClass(className: String): JsonNode? {
        return metaDataDtos
                .first { it.fullClassPath.equals(className, ignoreCase = true) }
                .jsonSchema
    }


}
