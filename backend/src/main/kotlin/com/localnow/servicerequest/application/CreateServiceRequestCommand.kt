package com.localnow.servicerequest.application

import java.time.Instant
import java.util.UUID

data class CreateServiceRequestCommand(
    val customerUserId: UUID,
    val categoryCode: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val requestedAt: Instant,
    val budgetAmountCents: Long,
    val currency: String,
)
