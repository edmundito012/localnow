package com.localnow.servicerequest.application

import com.localnow.professional.application.SearchProfessionalsService
import com.localnow.professional.persistence.ProfessionalSearchProjection
import com.localnow.professional.persistence.ServiceCategoryRepository
import com.localnow.servicerequest.domain.ServiceRequest
import com.localnow.servicerequest.persistence.ServiceRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.UUID

@Service
class ServiceRequestService(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val serviceCategoryRepository: ServiceCategoryRepository,
    private val searchProfessionalsService: SearchProfessionalsService,
    private val clock: Clock = Clock.systemUTC(),
) {

    @Transactional
    fun create(command: CreateServiceRequestCommand): ServiceRequest {
        val categoryCode = command.categoryCode.trim().uppercase()
        val category = serviceCategoryRepository.findById(categoryCode)
            .filter { it.active }
            .orElseThrow { ServiceCategoryUnavailableException(categoryCode) }

        val now = clock.instant()
        if (!command.requestedAt.isAfter(now)) {
            throw InvalidServiceRequestException("Requested time must be in the future")
        }

        val request = ServiceRequest.create(
            customerUserId = command.customerUserId,
            categoryCode = category.code,
            description = command.description,
            latitude = command.latitude,
            longitude = command.longitude,
            requestedAt = command.requestedAt,
            budgetAmountCents = command.budgetAmountCents,
            currency = command.currency,
            now = now,
        )

        return serviceRequestRepository.save(request)
    }

    @Transactional(readOnly = true)
    fun getOwned(requestId: UUID, customerUserId: UUID): ServiceRequest {
        val request = serviceRequestRepository.findById(requestId)
            .orElseThrow { ServiceRequestNotFoundException() }

        if (request.customerUserId != customerUserId) {
            throw ServiceRequestNotFoundException()
        }

        return request
    }

    @Transactional(readOnly = true)
    fun findCandidates(
        requestId: UUID,
        customerUserId: UUID,
        radiusMeters: Int,
    ): List<ProfessionalSearchProjection> {
        val request = getOwned(requestId, customerUserId)

        return searchProfessionalsService.search(
            latitude = request.latitude,
            longitude = request.longitude,
            radiusMeters = radiusMeters,
            categoryCode = request.categoryCode,
            availableAt = request.requestedAt,
        ).filter { candidate ->
            candidate.getUserId() != customerUserId
        }
    }
}
