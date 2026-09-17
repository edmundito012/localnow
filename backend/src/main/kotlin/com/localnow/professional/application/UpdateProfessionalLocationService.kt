package com.localnow.professional.application

import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdateProfessionalLocationService(
    private val professionalProfileRepository: ProfessionalProfileRepository,
) {

    @Transactional
    fun update(
        userId: UUID,
        latitude: Double,
        longitude: Double,
        serviceRadiusMeters: Int,
    ) {
        val updatedRows = professionalProfileRepository.updateServiceArea(
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            serviceRadiusMeters = serviceRadiusMeters,
        )

        if (updatedRows == 0) {
            throw ProfessionalProfileNotFoundException()
        }
    }
}
