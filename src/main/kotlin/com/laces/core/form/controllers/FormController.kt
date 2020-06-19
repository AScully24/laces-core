package com.laces.core.form.controllers

import com.laces.core.form.core.Form
import com.laces.core.form.dto.FormDto
import com.laces.core.form.dto.FormService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FormController(
        val formService: FormService
) {

    @GetMapping("api/form")
    fun getForms(@RequestParam name: String?, @RequestParam group: String?): List<FormDto> {
        val forms = run {
            name?.let { formService.findByName(it) }
                    ?: group?.let { formService.findByGroups(setOf(it)) }
                    ?: emptyList()
        }

        return forms.map(::toDto)
    }

    @GetMapping("api/form/flows")
    fun getFlow(@RequestParam flowName: String) = formService.findFlow(flowName)

    @GetMapping("api/form/flows/names")
    fun flows() = formService.getFlowNames()

    @GetMapping("api/form/names")
    fun names(): Set<String> = formService.getAllNames()

    @GetMapping("api/form/groups/names")
    fun groups() = formService.getAllGroups()

    fun toDto(form: Form): FormDto = form.run { FormDto(fullClassPath, jsonSchema, name, title, groups) }

}