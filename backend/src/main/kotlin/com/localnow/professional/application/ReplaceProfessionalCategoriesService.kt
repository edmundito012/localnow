package com.localnow.professional.application

import com.localnow.professional.domain.ServiceCategory
import com.localnow.professional.persistence.ProfessionalProfileRepository
import com.localnow.professional.persistence.ServiceCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ReplaceProfessionalCategoriesService(
    private val professionalProfileRepository: ProfessionalProfileRepository,
    private val serviceCategoryRepository: ServiceCategoryRepository,
) {

    @Transactional
    fun replace(userId: UUID, requestedCodes: Set<String>): List<ServiceCategory> {
        val normalizedCodes = requestedCodes
            .map { it.trim().uppercase() }
            .filter { it.isNotBlank() }
            .toSet()

        val categories = serviceCategoryRepository.findAllById(normalizedCodes)
            .filter { it.active }
        val acceptedCodes = categories.mapTo(mutableSetOf()) { it.code }
        val unknownCodes = normalizedCodes - acceptedCodes

        if (unknownCodes.isNotEmpty()) {
            throw UnknownServiceCategoriesException(unknownCodes)
        }

        val profile = professionalProfileRepository.findById(userId)
            .orElseThrow { ProfessionalProfileNotFoundException() }
        profile.replaceCategories(acceptedCodes)

        return categories.sortedBy { it.displayName }
    }

    @Transactional(readOnly = true)
    fun listActive(): List<ServiceCategory> =
        serviceCategoryRepository.findAllByActiveTrueOrderByDisplayNameAsc()
}
