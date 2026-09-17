package com.localnow.professional.api

import com.localnow.TestcontainersConfiguration
import com.localnow.identity.domain.UserRole
import com.localnow.identity.persistence.UserRepository
import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ProfessionalProfileControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val professionalProfileRepository: ProfessionalProfileRepository,
) {

    @Test
    fun `creates a profile for the authenticated user and grants the professional role`() {
        val token = registerAndLogin("new.professional@example.com")

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "Edmundo Reparaciones",
                  "phone": "+34600111222",
                  "bio": "Fontanería doméstica y reparaciones urgentes."
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
            jsonPath("$.displayName") { value("Edmundo Reparaciones") }
            jsonPath("$.phone") { value("+34600111222") }
            jsonPath("$.userId") { exists() }
        }

        val profile = professionalProfileRepository.findAll().single()
        val user = userRepository.findById(profile.userId).orElseThrow()

        assertThat(user.roles).contains(UserRole.CUSTOMER, UserRole.PROFESSIONAL)
    }

    @Test
    fun `rejects creating two professional profiles for the same user`() {
        val token = registerAndLogin("duplicate.professional@example.com")
        val body = """
            {
              "displayName": "Professional",
              "phone": "+34600999888"
            }
        """.trimIndent()

        repeat(1) {
            mockMvc.post("/api/v1/professionals/me") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isCreated() }
            }
        }

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("PROFESSIONAL_PROFILE_ALREADY_EXISTS") }
        }
    }

    @Test
    fun `requires authentication and validates the phone format`() {
        mockMvc.post("/api/v1/professionals/me") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "Anonymous",
                  "phone": "600111222"
                }
            """.trimIndent()
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    private fun registerAndLogin(email: String): String {
        val credentials = """
            {
              "email": "$email",
              "password": "a-secure-password"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = credentials
        }.andExpect {
            status { isCreated() }
        }

        val loginBody = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = credentials
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        return loginBody
            .substringAfter("\"accessToken\":\"")
            .substringBefore("\"")
            .also { require(it.isNotBlank()) }
    }
}
