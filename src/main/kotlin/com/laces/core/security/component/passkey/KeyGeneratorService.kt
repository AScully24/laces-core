package com.laces.core.security.component.passkey

import org.springframework.stereotype.Service
import javax.crypto.KeyGenerator
import javax.xml.bind.DatatypeConverter

@Service
class KeyGeneratorService {

    fun generateNewPassKey(): String {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        val secretKey = keyGen.generateKey()
        val encoded = secretKey.encoded
        return DatatypeConverter.printHexBinary(encoded).toLowerCase()
    }
}