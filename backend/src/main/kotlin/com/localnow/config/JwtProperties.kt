package com.localnow.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("security.jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String = "localnow",
    val accessTokenTtl: Duration = Duration.ofMinutes(15),
)
