package com.localnow.identity.application

import java.time.Instant

data class AccessToken(
    val value: String,
    val expiresAt: Instant,
)
