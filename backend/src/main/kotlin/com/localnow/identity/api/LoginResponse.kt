package com.localnow.identity.api

import com.localnow.identity.domain.UserRole
import java.util.UUID

data class LoginResponse(
    val userId: UUID,
    val email: String,
    val roles: Set<UserRole>,
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
)
