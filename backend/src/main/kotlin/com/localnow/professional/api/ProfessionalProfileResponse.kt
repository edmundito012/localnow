package com.localnow.professional.api

import java.time.Instant
import java.util.UUID

data class ProfessionalProfileResponse(
    val userId: UUID,
    val displayName: String,
    val phone: String,
    val bio: String?,
    val createdAt: Instant,
)
