package com.localnow.professional.api

import jakarta.validation.constraints.Size

data class ReplaceProfessionalCategoriesRequest(
    @field:Size(
        min = 1,
        max = 10,
        message = "Select between 1 and 10 service categories",
    )
    val categoryCodes: Set<String>,
)
