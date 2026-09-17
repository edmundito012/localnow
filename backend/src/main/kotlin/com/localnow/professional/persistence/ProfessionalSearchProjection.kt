package com.localnow.professional.persistence

import java.util.UUID

interface ProfessionalSearchProjection {
    fun getUserId(): UUID
    fun getDisplayName(): String
    fun getDistanceMeters(): Double
}
