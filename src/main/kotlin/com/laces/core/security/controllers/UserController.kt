package com.laces.core.security.controllers

import com.laces.core.security.component.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/user")
class UserController {

    companion object {
        const val API_KEY = "apiKey"
        const val API_KEY_URL = "transformation-api-key"
    }

    @Autowired
    lateinit var userService: UserService

    @GetMapping(API_KEY_URL)
    fun userApiKey(): Map<String,String> {
        return mapOf(API_KEY to userService.getCurrentUser().apiKey)
    }

    @PostMapping(API_KEY_URL)
    fun generateUserApiKey(): Map<String,String> {
        return mapOf(API_KEY to userService.generateNewApiKeyForCurrentUser())
    }
}