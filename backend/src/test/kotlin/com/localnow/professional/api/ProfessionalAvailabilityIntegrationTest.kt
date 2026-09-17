package com.localnow.professional.api

import com.localnow.TestcontainersConfiguration
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
class ProfessionalAvailabilityIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun `filters nearby professionals using their local weekly availability`() {
        val token = createSearchableProfessional()

        mockMvc.put("/api/v1/professionals/me/availability") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "timeZone": "Europe/Madrid",
                  "slots": [
                    {
                      "dayOfWeek": "MONDAY",
                      "startTime": "09:00:00",
                      "endTime": "14:00:00"
                    }
                  ]
                }
            """.trimIndent()
        }.andExpect {
            status { isOk() }
            jsonPath("$.timeZone") { value("Europe/Madrid") }
            jsonPath("$.slots[0].dayOfWeek") { value("MONDAY") }
        }

        mockMvc.get("/api/v1/professionals/search") {
            param("latitude", "40.4170")
            param("longitude", "-3.7040")
            param("radiusMeters", "5000")
            param("category", "PLUMBING")
            param("availableAt", "2026-09-21T08:30:00Z")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }

        mockMvc.get("/api/v1/professionals/search") {
            param("latitude", "40.4170")
            param("longitude", "-3.7040")
            param("radiusMeters", "5000")
            param("category", "PLUMBING")
            param("availableAt", "2026-09-21T15:00:00Z")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(0) }
        }
    }

    @Test
    fun `rejects overlapping availability slots`() {
        val token = createSearchableProfessional()

        mockMvc.put("/api/v1/professionals/me/availability") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "timeZone": "Europe/Madrid",
                  "slots": [
                    {
                      "dayOfWeek": "MONDAY",
                      "startTime": "09:00:00",
                      "endTime": "13:00:00"
                    },
                    {
                      "dayOfWeek": "MONDAY",
                      "startTime": "12:00:00",
                      "endTime": "15:00:00"
                    }
                  ]
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_AVAILABILITY") }
        }
    }

    private fun createSearchableProfessional(): String {
        val credentials = """
            {
              "email": "availability.professional@example.com",
              "password": "a-secure-password"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = credentials
        }.andExpect {
            status { isCreated() }
        }

        var token = login(credentials)

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "Available Professional",
                  "phone": "+34600333444"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
        }

        token = login(credentials)

        mockMvc.put("/api/v1/professionals/me/categories") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"categoryCodes":["PLUMBING"]}"""
        }.andExpect {
            status { isOk() }
        }

        mockMvc.put("/api/v1/professionals/me/location") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "latitude": 40.4168,
                  "longitude": -3.7038,
                  "serviceRadiusMeters": 10000
                }
            """.trimIndent()
        }.andExpect {
            status { isNoContent() }
        }

        return token
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
