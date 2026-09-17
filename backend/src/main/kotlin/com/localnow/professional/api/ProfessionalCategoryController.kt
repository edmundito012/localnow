package com.localnow.professional.api

import com.localnow.professional.application.ReplaceProfessionalCategoriesService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/professionals/me/categories")
class ProfessionalCategoryController(
    private val categoriesService: ReplaceProfessionalCategoriesService,
) {

    @PutMapping
    fun replace(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: ReplaceProfessionalCategoriesRequest,
    ): List<ServiceCategoryResponse> =
        categoriesService.replace(
            userId = UUID.fromString(jwt.subject),
            requestedCodes = request.categoryCodes,
        ).map {
            ServiceCategoryResponse(
                code = it.code,
                displayName = it.displayName,
            )
        }
}
