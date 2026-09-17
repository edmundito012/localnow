package com.localnow.professional.api

import com.localnow.professional.application.SearchProfessionalsService
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@Validated
@RestController
@RequestMapping("/api/v1/professionals/search")
class ProfessionalSearchController(
    private val searchProfessionalsService: SearchProfessionalsService,
) {

    @GetMapping
    fun search(
        @RequestParam
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        latitude: Double,

        @RequestParam
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        longitude: Double,

        @RequestParam(defaultValue = "10000")
        @Min(1000)
        @Max(50000)
        radiusMeters: Int,

        @RequestParam
        @NotBlank
        category: String,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        availableAt: Instant?,
    ): List<ProfessionalSearchResponse> =
        searchProfessionalsService.search(
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            categoryCode = category,
            availableAt = availableAt,
        ).map {
            ProfessionalSearchResponse.from(
                userId = it.getUserId(),
                displayName = it.getDisplayName(),
                distanceMeters = it.getDistanceMeters(),
            )
        }
}
