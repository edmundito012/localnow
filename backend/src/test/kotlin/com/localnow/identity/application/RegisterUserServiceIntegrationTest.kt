package com.localnow.identity.application

import com.localnow.TestcontainersConfiguration
import com.localnow.identity.domain.UserRole
import com.localnow.identity.persistence.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class RegisterUserServiceIntegrationTest(
    @Autowired private val registerUserService: RegisterUserService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val passwordEncoder: PasswordEncoder,
) {

    @Test
    fun `registers a customer with a normalized email and hashed password`() {
        val rawPassword = "a-secure-password"

        val userId = registerUserService.register(
            RegisterUserCommand(
                email = "  New.Customer@Example.com ",
                rawPassword = rawPassword,
            ),
        )

        val registeredUser = userRepository.findById(userId).orElseThrow()

        assertThat(registeredUser.email).isEqualTo("new.customer@example.com")
        assertThat(registeredUser.passwordHash).isNotEqualTo(rawPassword)
        assertThat(passwordEncoder.matches(rawPassword, registeredUser.passwordHash)).isTrue
        assertThat(registeredUser.roles).containsExactly(UserRole.CUSTOMER)
    }

    @Test
    fun `rejects an email that is already registered regardless of casing`() {
        val firstUserId = registerUserService.register(
            RegisterUserCommand(
                email = "existing@example.com",
                rawPassword = "first-password",
            ),
        )

        assertThatThrownBy {
            registerUserService.register(
                RegisterUserCommand(
                    email = " Existing@Example.com ",
                    rawPassword = "second-password",
                ),
            )
        }.isInstanceOf(EmailAlreadyRegisteredException::class.java)

        assertThat(userRepository.count()).isEqualTo(1)
        assertThat(userRepository.findById(firstUserId)).isPresent
    }
}
