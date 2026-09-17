package com.localnow.professional.application

import com.localnow.professional.domain.ProfessionalAvailability

data class ProfessionalAvailabilityConfiguration(
    val timeZone: String,
    val slots: List<ProfessionalAvailability>,
)
