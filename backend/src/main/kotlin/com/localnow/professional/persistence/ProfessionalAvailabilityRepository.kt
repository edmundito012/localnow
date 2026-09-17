package com.localnow.professional.persistence

import com.localnow.professional.domain.ProfessionalAvailability
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfessionalAvailabilityRepository : JpaRepository<ProfessionalAvailability, UUID> {
    fun deleteAllByProfessionalUserId(professionalUserId: UUID)

    fun findAllByProfessionalUserIdOrderByDayOfWeekAscStartTimeAsc(
        professionalUserId: UUID,
    ): List<ProfessionalAvailability>
}
