package com.localnow.servicerequest.api

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

data class CreateServiceRequestRequest(
    @field:NotBlank(message = "Category is required")
    val categoryCode: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 1000, message = "Description must contain at most 1000 characters")
    val description: String,

    @field:DecimalMin("-90.0")
    @field:DecimalMax("90.0")
    val latitude: Double,

    @field:DecimalMin("-180.0")
    @field:DecimalMax("180.0")
    val longitude: Double,

    @field:Future(message = "Requested time must be in the future")
    val requestedAt: Instant,

    @field:Min(value = 1, message = "Budget must be positive")
    val budgetAmountCents: Long,

    @field:Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a three-letter code")
    val currency: String = "EUR",
)
