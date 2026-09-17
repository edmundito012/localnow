package com.localnow.professional.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReplaceProfessionalAvailabilityRequest(
    @field:NotBlank(message = "Time zone is required")
    val timeZone: String,

    @field:Size(min = 1, max = 50, message = "Provide between 1 and 50 availability slots")
    @field:Valid
    val slots: List<AvailabilitySlotRequest>,
)
