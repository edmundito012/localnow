package com.localnow.professional.api

import java.util.UUID
import kotlin.math.roundToInt

data class ProfessionalSearchResponse(
    val userId: UUID,
    val displayName: String,
    val distanceMeters: Int,
) {
    companion object {
        fun from(
            userId: UUID,
            displayName: String,
            distanceMeters: Double,
        ) = ProfessionalSearchResponse(
            userId = userId,
            displayName = displayName,
            distanceMeters = distanceMeters.roundToInt(),
        )
    }
}
