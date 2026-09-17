package com.localnow.professional.api

data class ProfessionalAvailabilityResponse(
    val timeZone: String,
    val slots: List<AvailabilitySlotResponse>,
)
