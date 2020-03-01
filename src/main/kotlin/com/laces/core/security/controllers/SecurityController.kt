package com.laces.core.security.controllers

import com.laces.core.security.component.register.RegisterService
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.UserService
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("auth")
class SecurityController(
        val userService: UserService,
        val registerService: RegisterService
) {

    @PostMapping("success")
    fun success(): Map<String, String> {
        return mapOf("subscriptionState" to userService.getCurrentUser().subscriptionState.name)
    }

    @GetMapping("logout")
    fun logout(): ResponseEntity<Any> = ok().build()

    @GetMapping(value = ["validate"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun validateUser(@RequestBody newUser: NewUser) {
        registerService.validateNewUser(newUser)
    }

    @PostMapping(value = ["register"])
    fun completeRegistration(@RequestBody token: String): Map<String, String> {
        this.registerService.completeRegistration(token)
        return mapOf("message" to "Registration Completed")
    }

    @GetMapping("/logged-in")
    fun isLoggedIn(): ResponseEntity<Any> {
        if (userService.isUserLoggedIn()) {
            return ok().build()
        }

        return status(UNAUTHORIZED).build()
    }
}