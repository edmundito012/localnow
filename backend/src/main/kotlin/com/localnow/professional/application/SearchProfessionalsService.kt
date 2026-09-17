package com.localnow.professional.application

import com.localnow.professional.persistence.ProfessionalSearchProjection
import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SearchProfessionalsService(
    private val professionalProfileRepository: ProfessionalProfileRepository,
) {

    @Transactional(readOnly = true)
    fun search(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        categoryCode: String,
        availableAt: Instant?,
    ): List<ProfessionalSearchProjection> {
        val normalizedCategory = categoryCode.trim().uppercase()

        return if (availableAt == null) {
            professionalProfileRepository.searchNearby(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                categoryCode = normalizedCategory,
            )
        } else {
            professionalProfileRepository.searchNearbyAvailable(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                categoryCode = normalizedCategory,
                availableAt = availableAt,
            )
        }
    }
}
