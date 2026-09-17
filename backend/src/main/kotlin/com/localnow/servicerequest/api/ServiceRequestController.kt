package com.localnow.servicerequest.api

import com.localnow.professional.api.ProfessionalSearchResponse
import com.localnow.servicerequest.application.CreateServiceRequestCommand
import com.localnow.servicerequest.application.ServiceRequestService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/v1/service-requests")
class ServiceRequestController(
    private val serviceRequestService: ServiceRequestService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: CreateServiceRequestRequest,
    ): ServiceRequestResponse =
        ServiceRequestResponse.from(
            serviceRequestService.create(
                CreateServiceRequestCommand(
                    customerUserId = UUID.fromString(jwt.subject),
                    categoryCode = request.categoryCode,
                    description = request.description,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    requestedAt = request.requestedAt,
                    budgetAmountCents = request.budgetAmountCents,
                    currency = request.currency,
                ),
            ),
        )

    @GetMapping("/{requestId}")
    fun get(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable requestId: UUID,
    ): ServiceRequestResponse =
        ServiceRequestResponse.from(
            serviceRequestService.getOwned(
                requestId = requestId,
                customerUserId = UUID.fromString(jwt.subject),
            ),
        )

    @GetMapping("/{requestId}/candidates")
    fun candidates(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable requestId: UUID,
        @RequestParam(defaultValue = "10000")
        @Min(1000)
        @Max(50000)
        radiusMeters: Int,
    ): List<ProfessionalSearchResponse> =
        serviceRequestService.findCandidates(
            requestId = requestId,
            customerUserId = UUID.fromString(jwt.subject),
            radiusMeters = radiusMeters,
        ).map {
            ProfessionalSearchResponse.from(
                userId = it.getUserId(),
                displayName = it.getDisplayName(),
                distanceMeters = it.getDistanceMeters(),
            )
        }
}
