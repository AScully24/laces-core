package com.laces.core.security.component.token

import java.util.*

interface HasExpiryToken {
    val token: String
    val expiryDate: Date
}