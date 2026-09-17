package com.localnow.professional.api

import com.localnow.professional.application.ReplaceProfessionalCategoriesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/service-categories")
class ServiceCategoryController(
    private val categoriesService: ReplaceProfessionalCategoriesService,
) {

    @GetMapping
    fun listActive(): List<ServiceCategoryResponse> =
        categoriesService.listActive().map {
            ServiceCategoryResponse(
                code = it.code,
                displayName = it.displayName,
            )
        }
}
