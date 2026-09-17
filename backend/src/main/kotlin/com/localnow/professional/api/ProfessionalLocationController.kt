package com.localnow.professional.api

import com.localnow.professional.application.UpdateProfessionalLocationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/professionals/me/location")
class ProfessionalLocationController(
    private val updateProfessionalLocationService: UpdateProfessionalLocationService,
) {

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: UpdateProfessionalLocationRequest,
    ) {
        updateProfessionalLocationService.update(
            userId = UUID.fromString(jwt.subject),
            latitude = request.latitude,
            longitude = request.longitude,
            serviceRadiusMeters = request.serviceRadiusMeters,
        )
    }
}
