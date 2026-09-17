package com.localnow.servicerequest.persistence

import com.localnow.servicerequest.domain.ServiceRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface ServiceRequestRepository : JpaRepository<ServiceRequest, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            UPDATE service_request
            SET status = 'MATCHED',
                updated_at = :acceptedAt
            WHERE id = :requestId
              AND status = 'OPEN'
        """,
        nativeQuery = true,
    )
    fun claimIfOpen(
        @Param("requestId") requestId: UUID,
        @Param("acceptedAt") acceptedAt: Instant,
    ): Int
}
