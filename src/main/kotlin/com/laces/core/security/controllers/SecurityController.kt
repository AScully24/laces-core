package com.laces.core.security.controllers

import com.laces.core.security.component.register.RegisterService
import com.laces.core.security.component.user.NewUser
import com.laces.core.security.component.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("auth")
class SecurityController(
        val userService: UserService,
        val registerService: RegisterService
) {

    @PostMapping("success")
    fun success(): ResponseEntity<Any> = ResponseEntity.ok().build()

    @PostMapping("failure")
    fun failure(): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @GetMapping("logout")
    fun logout(): ResponseEntity<Any> = ResponseEntity.ok().build()

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
        if (this.userService.isUserLoggedIn()) {
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}