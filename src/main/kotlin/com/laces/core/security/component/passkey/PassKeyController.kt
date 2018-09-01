package com.laces.core.security.component.passkey

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pass-key")
class PassKeyController {

    @Autowired
    lateinit var keyGeneratorService : KeyGeneratorService

    @GetMapping
    fun generateNewKey(): String {
        return keyGeneratorService.generateNewPassKey()
    }
}