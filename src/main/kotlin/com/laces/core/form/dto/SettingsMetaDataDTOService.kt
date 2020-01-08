package com.laces.core.form.dto

import com.laces.core.form.core.FormMetaDataService
import com.laces.core.responses.ResourceNotFoundException
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class SettingsMetaDataDTOService(
        private val formMetaDataService: FormMetaDataService
) {

    private val formMetaDataResponses: List<FormMetaDataResponse>

    init {
        val mapper = ModelMapper()
        formMetaDataResponses = formMetaDataService.settingsMetaData
                .map { mapper.map(it, FormMetaDataResponse::class.java) }
    }

    fun getMetaData(): List<FormMetaDataResponse> {
        return formMetaDataResponses
    }

    fun getMetaData(group: String): List<FormMetaDataResponse> {
        return formMetaDataResponses.filter { it.groups.any { itGroup -> itGroup == group } }
    }

    fun getFlow(flowName: String): FlowResponse {
        return formMetaDataService.getFlow(flowName) ?: throw ResourceNotFoundException("Unable to find flow: $flowName")
    }

    fun getMetaDataContainingAll(vararg groups: String): List<FormMetaDataResponse> {
        return formMetaDataResponses.filter { it.groups.containsAll(groups.toList()) }
    }

    fun findAllPublicSettings(): List<FormMetaDataResponse> {
        return formMetaDataResponses.filter { it.public }
    }

    fun findAllPublicSettings(group: String): List<FormMetaDataResponse> {
        return findAllPublicSettings().filter { it.groups.contains(group) }
    }

    fun findPublicFormByName(name: String): FormMetaDataResponse {
        return findAllPublicSettings().first { it.name == name }
    }
}
