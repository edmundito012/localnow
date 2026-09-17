package com.localnow.professional.persistence

import com.localnow.professional.domain.ProfessionalProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfessionalProfileRepository : JpaRepository<ProfessionalProfile, UUID> {
    fun existsByPhone(phone: String): Boolean
}
