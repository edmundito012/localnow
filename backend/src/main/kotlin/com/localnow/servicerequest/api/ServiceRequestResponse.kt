package com.localnow.servicerequest.api

import com.localnow.servicerequest.domain.ServiceRequest
import com.localnow.servicerequest.domain.ServiceRequestStatus
import java.time.Instant
import java.util.UUID

data class ServiceRequestResponse(
    val id: UUID,
    val customerUserId: UUID,
    val categoryCode: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val requestedAt: Instant,
    val budgetAmountCents: Long,
    val currency: String,
    val status: ServiceRequestStatus,
    val createdAt: Instant,
) {
    companion object {
        fun from(request: ServiceRequest) = ServiceRequestResponse(
            id = request.id,
            customerUserId = request.customerUserId,
            categoryCode = request.categoryCode,
            description = request.description,
            latitude = request.latitude,
            longitude = request.longitude,
            requestedAt = request.requestedAt,
            budgetAmountCents = request.budgetAmountCents,
            currency = request.currency,
            status = request.status,
            createdAt = request.createdAt,
        )
    }
}
