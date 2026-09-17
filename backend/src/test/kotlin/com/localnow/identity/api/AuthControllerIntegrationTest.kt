package com.localnow.identity.api

import com.localnow.TestcontainersConfiguration
import com.localnow.identity.persistence.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class AuthControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepository: UserRepository,
) {

    @Test
    fun `registers a customer through the public API`() {
        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "email": "api.customer@example.com",
                  "password": "a-secure-password"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
            jsonPath("$.userId") { exists() }
        }

        assertThat(userRepository.existsByEmail("api.customer@example.com")).isTrue
    }

    @Test
    fun `returns conflict when the email is already registered`() {
        val requestBody = """
            {
              "email": "duplicate@example.com",
              "password": "a-secure-password"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
        }

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("EMAIL_ALREADY_REGISTERED") }
        }
    }

    @Test
    fun `returns validation errors for an invalid request`() {
        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "email": "not-an-email",
                  "password": "short"
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("VALIDATION_FAILED") }
            jsonPath("$.fieldErrors.length()") { value(2) }
        }
    }

    @Test
    fun `returns a bearer token for valid credentials and accepts it on protected endpoints`() {
        val registerBody = """
            {
              "email": "login.api@example.com",
              "password": "correct-password"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = registerBody
        }.andExpect {
            status { isCreated() }
        }

        val loginBody = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = registerBody
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value("login.api@example.com") }
            jsonPath("$.roles[0]") { value("CUSTOMER") }
            jsonPath("$.accessToken") { isNotEmpty() }
            jsonPath("$.tokenType") { value("Bearer") }
            jsonPath("$.expiresInSeconds") { value(900) }
            jsonPath("$.passwordHash") { doesNotExist() }
        }.andReturn().response.contentAsString

        val token = loginBody
            .substringAfter("\"accessToken\":\"")
            .substringBefore("\"")
        require(token.isNotBlank()) { "Login response does not contain an access token" }

        mockMvc.get("/api/v1/auth/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value("login.api@example.com") }
            jsonPath("$.roles[0]") { value("CUSTOMER") }
        }
    }

    @Test
    fun `rejects protected endpoints without a bearer token`() {
        mockMvc.get("/api/v1/auth/me").andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `returns unauthorized for invalid credentials`() {
        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "email": "unknown@example.com",
                  "password": "incorrect-password"
                }
            """.trimIndent()
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("INVALID_CREDENTIALS") }
            jsonPath("$.message") { value("Email or password is incorrect") }
        }
    }
}
