package com.localnow.professional.api

import com.localnow.professional.application.AvailabilitySlotCommand
import com.localnow.professional.application.ProfessionalAvailabilityConfiguration
import com.localnow.professional.application.ReplaceProfessionalAvailabilityService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/professionals/me/availability")
class ProfessionalAvailabilityController(
    private val availabilityService: ReplaceProfessionalAvailabilityService,
) {

    @PutMapping
    fun replace(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: ReplaceProfessionalAvailabilityRequest,
    ): ProfessionalAvailabilityResponse =
        availabilityService.replace(
            userId = UUID.fromString(jwt.subject),
            timeZone = request.timeZone,
            requestedSlots = request.slots.map {
                AvailabilitySlotCommand(
                    dayOfWeek = it.dayOfWeek,
                    startTime = it.startTime,
                    endTime = it.endTime,
                )
            },
        ).toResponse()

    @GetMapping
    fun get(
        @AuthenticationPrincipal jwt: Jwt,
    ): ProfessionalAvailabilityResponse =
        availabilityService.get(UUID.fromString(jwt.subject)).toResponse()

    private fun ProfessionalAvailabilityConfiguration.toResponse() =
        ProfessionalAvailabilityResponse(
            timeZone = timeZone,
            slots = slots.map {
                AvailabilitySlotResponse(
                    dayOfWeek = it.day(),
                    startTime = it.startTime,
                    endTime = it.endTime,
                )
            },
        )
}
