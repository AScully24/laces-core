package com.laces.core.security.component.register

import com.laces.core.security.component.user.AdditionalInfo

/**
 * When implementing this interface, throw runtime exceptions describing the failure. The API will handle the rest.
 * If no validate is required, implement an empty validate method with the correct key name.
 */
interface AdditionalInfoValidator{
    fun validate(additionalInfo: AdditionalInfo?)
}