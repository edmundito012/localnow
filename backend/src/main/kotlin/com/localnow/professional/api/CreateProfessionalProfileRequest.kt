package com.localnow.professional.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateProfessionalProfileRequest(
    @field:NotBlank(message = "Display name is required")
    @field:Size(max = 120, message = "Display name must contain at most 120 characters")
    val displayName: String,

    @field:NotBlank(message = "Phone is required")
    @field:Pattern(
        regexp = "^\\+[1-9]\\d{7,14}$",
        message = "Phone must use E.164 format, for example +34600111222",
    )
    val phone: String,

    @field:Size(max = 1000, message = "Bio must contain at most 1000 characters")
    val bio: String? = null,
)
