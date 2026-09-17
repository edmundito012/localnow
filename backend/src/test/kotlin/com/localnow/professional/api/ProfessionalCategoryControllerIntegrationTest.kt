package com.localnow.professional.api

import com.localnow.TestcontainersConfiguration
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ProfessionalCategoryControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val professionalProfileRepository: ProfessionalProfileRepository,
) {

    @Test
    fun `lists the active category catalog without authentication`() {
        mockMvc.get("/api/v1/service-categories").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(3) }
            jsonPath("$[0].code") { exists() }
            jsonPath("$[0].displayName") { exists() }
        }
    }

    @Test
    fun `replaces the authenticated professional categories idempotently`() {
        val token = registerCreateProfileAndLoginAgain()

        repeat(2) {
            mockMvc.put("/api/v1/professionals/me/categories") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = """
                    {
                      "categoryCodes": ["plumbing", "ELECTRICAL"]
                    }
                """.trimIndent()
            }.andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
            }
        }

        val profile = professionalProfileRepository.findAll().single()
        assertThat(profile.categoryCodes)
            .containsExactlyInAnyOrder("PLUMBING", "ELECTRICAL")
    }

    @Test
    fun `rejects unknown categories without changing the profile`() {
        val token = registerCreateProfileAndLoginAgain()

        mockMvc.put("/api/v1/professionals/me/categories") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "categoryCodes": ["PLUMBING", "UNKNOWN"]
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("UNKNOWN_SERVICE_CATEGORIES") }
        }

        assertThat(professionalProfileRepository.findAll().single().categoryCodes).isEmpty()
    }

    private fun registerCreateProfileAndLoginAgain(): String {
        val credentials = """
            {
              "email": "categories.professional@example.com",
              "password": "a-secure-password"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = credentials
        }.andExpect {
            status { isCreated() }
        }

        val customerToken = login(credentials)

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $customerToken")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "Category Professional",
                  "phone": "+34600777888"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
        }

        return login(credentials)
    }

    private fun login(credentials: String): String {
        val body = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = credentials
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        return body
            .substringAfter("\"accessToken\":\"")
            .substringBefore("\"")
            .also { require(it.isNotBlank()) }
    }
}
