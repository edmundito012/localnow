package com.localnow.identity.application

import com.localnow.identity.domain.User
import com.localnow.identity.persistence.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

@Service
class RegisterUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun register(command: RegisterUserCommand): UUID {
        val normalizedEmail = command.email.trim().lowercase()

        require(normalizedEmail.isNotBlank()) { "Email must not be blank" }
        require(command.rawPassword.length >= MINIMUM_PASSWORD_CHARACTERS) {
            "Password must contain at least $MINIMUM_PASSWORD_CHARACTERS characters"
        }
        require(command.rawPassword.toByteArray(UTF_8).size <= BCRYPT_MAXIMUM_PASSWORD_BYTES) {
            "Password must not exceed $BCRYPT_MAXIMUM_PASSWORD_BYTES UTF-8 bytes"
        }
        require(command.roles.isNotEmpty()) { "A user must have at least one role" }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw EmailAlreadyRegisteredException(normalizedEmail)
        }

        val user = User.create(
            email = normalizedEmail,
            passwordHash = passwordEncoder.encode(command.rawPassword),
            roles = command.roles,
        )

        return userRepository.save(user).id
    }

    private companion object {
        const val MINIMUM_PASSWORD_CHARACTERS = 8
        const val BCRYPT_MAXIMUM_PASSWORD_BYTES = 72
    }
}
