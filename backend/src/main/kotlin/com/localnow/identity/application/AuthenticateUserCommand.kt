package com.localnow.identity.application

data class AuthenticateUserCommand(
    val email: String,
    val rawPassword: String,
)
