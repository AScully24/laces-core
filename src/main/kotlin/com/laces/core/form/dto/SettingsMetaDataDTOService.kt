package com.laces.core.form.dto

import com.fasterxml.jackson.databind.JsonNode
import com.laces.core.form.core.SettingsMetaDataService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class SettingsMetaDataDTOService(
        private val settingsMetaDataService: SettingsMetaDataService
) {

    private val formMetaDataResponses: List<FormMetaDataResponse>

    init {
        val mapper = ModelMapper()
        formMetaDataResponses = settingsMetaDataService.settingsMetaData
                .map { mapper.map(it, FormMetaDataResponse::class.java) }
    }

    fun getMetaData(): List<FormMetaDataResponse> {
        return formMetaDataResponses
    }

    fun getMetaData(group: String): List<FormMetaDataResponse> {
        return formMetaDataResponses.filter { it.groups.any { itGroup -> itGroup == group } }
    }

    fun getFlow(flowName: String): FlowResponse {

        val flowStepResponses = settingsMetaDataService.getFlow(flowName)
//        formMetaDataResponses
//                .filter { isInFlow(it, flowName) }
//                .groupBy { it.flowSteps.first { flowStep -> flowName == flowStep.flow }.stepNumber  }
//                .map { (stepNumber, formSchemas) -> FlowStepResponse(stepNumber, flowName,formSchemas.map { it.jsonSchema }) }

        return FlowResponse(flowStepResponses)
    }

    private fun isInFlow(it: FormMetaDataResponse, flowName: String) =
            it.flowSteps.any { flowStep -> flowStep.flow == flowName }

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

    fun findSchemaForClass(className: String): JsonNode? {
        return formMetaDataResponses
                .first { it.fullClassPath.equals(className, ignoreCase = true) }
                .jsonSchema
    }


}
