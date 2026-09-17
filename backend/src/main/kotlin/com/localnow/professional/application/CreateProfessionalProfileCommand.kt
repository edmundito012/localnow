package com.localnow.professional.application

import java.util.UUID

data class CreateProfessionalProfileCommand(
    val userId: UUID,
    val displayName: String,
    val phone: String,
    val bio: String?,
)
