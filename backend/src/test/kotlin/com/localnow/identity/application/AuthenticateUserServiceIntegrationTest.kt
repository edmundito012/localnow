package com.localnow.identity.application

import com.localnow.TestcontainersConfiguration
import com.localnow.identity.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class AuthenticateUserServiceIntegrationTest(
    @Autowired private val registerUserService: RegisterUserService,
    @Autowired private val authenticateUserService: AuthenticateUserService,
) {

    @Test
    fun `authenticates a registered user`() {
        val userId = registerUserService.register(
            RegisterUserCommand(
                email = " Login.User@Example.com ",
                rawPassword = "correct-password",
            ),
        )

        val authenticatedUser = authenticateUserService.authenticate(
            AuthenticateUserCommand(
                email = "login.user@example.com",
                rawPassword = "correct-password",
            ),
        )

        assertThat(authenticatedUser.userId).isEqualTo(userId)
        assertThat(authenticatedUser.email).isEqualTo("login.user@example.com")
        assertThat(authenticatedUser.roles).containsExactly(UserRole.CUSTOMER)
    }

    @Test
    fun `rejects an incorrect password without exposing the reason`() {
        registerUserService.register(
            RegisterUserCommand(
                email = "wrong.password@example.com",
                rawPassword = "correct-password",
            ),
        )

        assertThatThrownBy {
            authenticateUserService.authenticate(
                AuthenticateUserCommand(
                    email = "wrong.password@example.com",
                    rawPassword = "incorrect-password",
                ),
            )
        }.isInstanceOf(InvalidCredentialsException::class.java)
            .hasMessage("Email or password is incorrect")
    }

    @Test
    fun `rejects an unknown email with the same error`() {
        assertThatThrownBy {
            authenticateUserService.authenticate(
                AuthenticateUserCommand(
                    email = "unknown@example.com",
                    rawPassword = "any-password",
                ),
            )
        }.isInstanceOf(InvalidCredentialsException::class.java)
            .hasMessage("Email or password is incorrect")
    }
}
