package com.localnow.job.application

import com.localnow.job.domain.Job
import com.localnow.job.persistence.JobRepository
import com.localnow.professional.application.SearchProfessionalsService
import com.localnow.professional.persistence.ProfessionalProfileRepository
import com.localnow.servicerequest.application.ServiceRequestNotFoundException
import com.localnow.servicerequest.persistence.ServiceRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.UUID

@Service
class JobService(
    private val jobRepository: JobRepository,
    private val serviceRequestRepository: ServiceRequestRepository,
    private val professionalProfileRepository: ProfessionalProfileRepository,
    private val searchProfessionalsService: SearchProfessionalsService,
    private val clock: Clock = Clock.systemUTC(),
) {

    @Transactional
    fun accept(requestId: UUID, professionalUserId: UUID): Job {
        val request = serviceRequestRepository.findById(requestId)
            .orElseThrow { ServiceRequestNotFoundException() }

        if (
            request.customerUserId == professionalUserId ||
            !professionalProfileRepository.existsById(professionalUserId)
        ) {
            throw ProfessionalNotEligibleException()
        }

        val isEligible = searchProfessionalsService.search(
            latitude = request.latitude,
            longitude = request.longitude,
            radiusMeters = 50_000,
            categoryCode = request.categoryCode,
            availableAt = request.requestedAt,
        ).any { it.getUserId() == professionalUserId }

        if (!isEligible) {
            throw ProfessionalNotEligibleException()
        }

        val now = clock.instant()
        if (serviceRequestRepository.claimIfOpen(requestId, now) != 1) {
            throw ServiceRequestAlreadyClaimedException()
        }

        return jobRepository.save(
            Job.accepted(
                request = request,
                professionalUserId = professionalUserId,
                now = now,
            ),
        )
    }

    @Transactional(readOnly = true)
    fun get(jobId: UUID, authenticatedUserId: UUID): Job {
        val job = jobRepository.findById(jobId)
            .orElseThrow { JobNotFoundException() }

        if (
            authenticatedUserId != job.customerUserId &&
            authenticatedUserId != job.professionalUserId
        ) {
            throw JobNotFoundException()
        }

        return job
    }
}
