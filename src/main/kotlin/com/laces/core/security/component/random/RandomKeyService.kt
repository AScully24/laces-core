package com.laces.core.security.component.random

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.crypto.KeyGenerator
import javax.xml.bind.DatatypeConverter

@Service
class RandomKeyService {

    val passwordEncoder = BCryptPasswordEncoder()

    fun matches(apiKey: String, secret: String): Boolean {
        return passwordEncoder.matches(apiKey, secret)
    }

    fun generateNewApiKey(apiKey: String = generateRandomKey()): GeneratedKey {
        val secret = passwordEncoder.encode(apiKey)
        return GeneratedKey(apiKey, secret)
    }

    fun generateRandomKey(): String {
        val keyGen = KeyGenerator.getInstance("AES")
                .also { it.init(128) }
        val secretKey = keyGen.generateKey()
        val encoded = secretKey.encoded
        return DatatypeConverter.printHexBinary(encoded).toLowerCase()
    }
}