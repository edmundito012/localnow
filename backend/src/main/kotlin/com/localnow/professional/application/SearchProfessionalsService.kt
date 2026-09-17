package com.localnow.professional.application

import com.localnow.professional.persistence.ProfessionalSearchProjection
import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    ): List<ProfessionalSearchProjection> =
        professionalProfileRepository.searchNearby(
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            categoryCode = categoryCode.trim().uppercase(),
        )
}
