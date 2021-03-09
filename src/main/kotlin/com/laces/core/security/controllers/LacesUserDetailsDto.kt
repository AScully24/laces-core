package com.laces.core.security.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle
import com.laces.core.form.core.FormAnnotations.FormData
import com.laces.core.security.component.user.subscription.SubscriptionState

@FormData(isPublic = true, name="UserDetails")
@JsonSchemaTitle("User Account")
data class LacesUserDetailsDto(
        @field:JsonProperty(value = "Username")
        val username: String,

        @field:JsonProperty(value = "Subscription State")
        val subscriptionState: SubscriptionState,

        @field:JsonProperty(value = "Subscription Plan")
        val planName: String,

        @field:JsonProperty(value = "Extra")
        val additionalInfo: Map<String, Any>?
)