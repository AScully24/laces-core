package com.laces.core.security.controllers

import com.laces.core.security.component.register.RegisterService
import com.laces.core.security.component.user.NewUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * This controller won't get loaded when app.stripe property is set to true.
 */
@RestController
@RequestMapping("auth")
@ConditionalOnProperty(name = ["app.stripe.enabled"],havingValue= "false")
class SimpleRegisterController {

    @Autowired
    lateinit var registerService: RegisterService

    @PutMapping(value = ["register"],consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun register(@RequestBody newUser: NewUser): Map<String, String> {
        this.registerService.registerNewUser(newUser)
        return mapOf("message" to "Registration email sent")
    }
}