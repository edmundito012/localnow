package com.localnow.job.domain

import com.localnow.servicerequest.domain.ServiceRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "job")
class Job private constructor(
    @Id
    val id: UUID,

    @Column(name = "service_request_id", nullable = false, unique = true, updatable = false)
    val serviceRequestId: UUID,

    @Column(name = "customer_user_id", nullable = false, updatable = false)
    val customerUserId: UUID,

    @Column(name = "professional_user_id", nullable = false, updatable = false)
    val professionalUserId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    val status: JobStatus,

    @Column(name = "agreed_amount_cents", nullable = false)
    val agreedAmountCents: Long,

    @Column(nullable = false, length = 3)
    val currency: String,

    @Column(name = "accepted_at", nullable = false, updatable = false)
    val acceptedAt: Instant,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) {
    companion object {
        fun accepted(
            request: ServiceRequest,
            professionalUserId: UUID,
            now: Instant,
        ): Job {
            require(request.customerUserId != professionalUserId) {
                "Customer and professional must be different users"
            }

            return Job(
                id = UUID.randomUUID(),
                serviceRequestId = request.id,
                customerUserId = request.customerUserId,
                professionalUserId = professionalUserId,
                status = JobStatus.ACCEPTED,
                agreedAmountCents = request.budgetAmountCents,
                currency = request.currency,
                acceptedAt = now,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
