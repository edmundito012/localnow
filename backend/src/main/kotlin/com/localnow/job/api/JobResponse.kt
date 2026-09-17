package com.localnow.job.api

import com.localnow.job.domain.Job
import com.localnow.job.domain.JobStatus
import java.time.Instant
import java.util.UUID

data class JobResponse(
    val id: UUID,
    val serviceRequestId: UUID,
    val customerUserId: UUID,
    val professionalUserId: UUID,
    val status: JobStatus,
    val agreedAmountCents: Long,
    val currency: String,
    val acceptedAt: Instant,
) {
    companion object {
        fun from(job: Job) = JobResponse(
            id = job.id,
            serviceRequestId = job.serviceRequestId,
            customerUserId = job.customerUserId,
            professionalUserId = job.professionalUserId,
            status = job.status,
            agreedAmountCents = job.agreedAmountCents,
            currency = job.currency,
            acceptedAt = job.acceptedAt,
        )
    }
}
