package com.localnow.servicerequest.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "service_request")
class ServiceRequest private constructor(
    @Id
    val id: UUID,

    @Column(name = "customer_user_id", nullable = false, updatable = false)
    val customerUserId: UUID,

    @Column(name = "category_code", nullable = false, length = 50)
    val categoryCode: String,

    @Column(nullable = false, length = 1000)
    val description: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(name = "requested_at", nullable = false)
    val requestedAt: Instant,

    @Column(name = "budget_amount_cents", nullable = false)
    val budgetAmountCents: Long,

    @Column(nullable = false, length = 3)
    val currency: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    val status: ServiceRequestStatus,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) {
    companion object {
        fun create(
            customerUserId: UUID,
            categoryCode: String,
            description: String,
            latitude: Double,
            longitude: Double,
            requestedAt: Instant,
            budgetAmountCents: Long,
            currency: String,
            now: Instant = Instant.now(),
        ): ServiceRequest {
            val normalizedDescription = description.trim()
            val normalizedCategory = categoryCode.trim().uppercase()
            val normalizedCurrency = currency.trim().uppercase()

            require(normalizedDescription.isNotBlank()) { "Description must not be blank" }
            require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
            require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
            require(budgetAmountCents > 0) { "Budget must be positive" }

            return ServiceRequest(
                id = UUID.randomUUID(),
                customerUserId = customerUserId,
                categoryCode = normalizedCategory,
                description = normalizedDescription,
                latitude = latitude,
                longitude = longitude,
                requestedAt = requestedAt,
                budgetAmountCents = budgetAmountCents,
                currency = normalizedCurrency,
                status = ServiceRequestStatus.OPEN,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
