package com.localnow.professional.persistence

import com.localnow.professional.domain.ServiceCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceCategoryRepository : JpaRepository<ServiceCategory, String> {
    fun findAllByActiveTrueOrderByDisplayNameAsc(): List<ServiceCategory>
}
