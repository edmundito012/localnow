package com.localnow.identity.application

import com.localnow.config.JwtProperties
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class JwtTokenService(
    private val jwtEncoder: JwtEncoder,
    private val properties: JwtProperties,
    private val clock: Clock = Clock.systemUTC(),
) {

    fun issueFor(user: AuthenticatedUser): AccessToken {
        val issuedAt = clock.instant()
        val expiresAt = issuedAt.plus(properties.accessTokenTtl)
        val claims = JwtClaimsSet.builder()
            .issuer(properties.issuer)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .subject(user.userId.toString())
            .claim("email", user.email)
            .claim("roles", user.roles.map { it.name }.sorted())
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build()
        val token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims))

        return AccessToken(token.tokenValue, expiresAt)
    }
}
