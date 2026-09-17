package com.localnow.professional.api

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class UpdateProfessionalLocationRequest(
    @field:DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    val latitude: Double,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    val longitude: Double,

    @field:Min(value = 1000, message = "Service radius must be at least 1000 metres")
    @field:Max(value = 100000, message = "Service radius must be at most 100000 metres")
    val serviceRadiusMeters: Int,
)
