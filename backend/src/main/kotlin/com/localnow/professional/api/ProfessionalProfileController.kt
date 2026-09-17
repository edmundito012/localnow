package com.localnow.professional.api

import com.localnow.professional.application.CreateProfessionalProfileCommand
import com.localnow.professional.application.CreateProfessionalProfileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/professionals")
class ProfessionalProfileController(
    private val createProfessionalProfileService: CreateProfessionalProfileService,
) {

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: CreateProfessionalProfileRequest,
    ): ProfessionalProfileResponse {
        val profile = createProfessionalProfileService.create(
            CreateProfessionalProfileCommand(
                userId = UUID.fromString(jwt.subject),
                displayName = request.displayName,
                phone = request.phone,
                bio = request.bio,
            ),
        )

        return ProfessionalProfileResponse(
            userId = profile.userId,
            displayName = profile.displayName,
            phone = profile.phone,
            bio = profile.bio,
            createdAt = profile.createdAt,
        )
    }
}
