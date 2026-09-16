package com.localnow.identity.application

import com.localnow.identity.domain.UserRole

data class RegisterUserCommand(
    val email: String,
    val rawPassword: String,
    val roles: Set<UserRole> = setOf(UserRole.CUSTOMER),
)
