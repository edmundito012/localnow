package com.localnow.identity.application

import com.localnow.identity.domain.UserRole
import java.util.UUID

data class AuthenticatedUser(
    val userId: UUID,
    val email: String,
    val roles: Set<UserRole>,
)
