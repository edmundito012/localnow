package com.localnow.identity.api

import java.util.UUID

data class CurrentUserResponse(
    val userId: UUID,
    val email: String,
    val roles: List<String>,
)
