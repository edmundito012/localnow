package com.localnow.identity.application

import com.localnow.identity.persistence.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticateUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional(readOnly = true)
    fun authenticate(command: AuthenticateUserCommand): AuthenticatedUser {
        val normalizedEmail = command.email.trim().lowercase()
        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw InvalidCredentialsException()

        if (!user.enabled || !passwordEncoder.matches(command.rawPassword, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return AuthenticatedUser(
            userId = user.id,
            email = user.email,
            roles = user.roles.toSet(),
        )
    }
}
