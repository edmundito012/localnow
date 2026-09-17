package com.localnow.servicerequest.api

import com.localnow.TestcontainersConfiguration
import com.localnow.servicerequest.persistence.ServiceRequestRepository
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ServiceRequestIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val serviceRequestRepository: ServiceRequestRepository,
) {

    @Test
    fun `creates a customer request and generates available nearby candidates`() {
        val zone = ZoneId.of("Europe/Madrid")
        val requestedAt = ZonedDateTime.of(
            LocalDate.now(zone).plusDays(3),
            LocalTime.of(11, 0),
            zone,
        ).toInstant()

        createAvailableProfessional(requestedAt, zone)
        val customerToken = registerAndLogin(
            email = "request.customer@example.com",
            password = "a-secure-password",
        )

        val responseBody = mockMvc.post("/api/v1/service-requests") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $customerToken")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "categoryCode": "plumbing",
                  "description": "Water is leaking below the kitchen sink",
                  "latitude": 40.4170,
                  "longitude": -3.7040,
                  "requestedAt": "$requestedAt",
                  "budgetAmountCents": 12000,
                  "currency": "eur"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
            jsonPath("$.categoryCode") { value("PLUMBING") }
            jsonPath("$.budgetAmountCents") { value(12000) }
            jsonPath("$.currency") { value("EUR") }
            jsonPath("$.status") { value("OPEN") }
        }.andReturn().response.contentAsString

        val requestId = responseBody
            .substringAfter("\"id\":\"")
            .substringBefore("\"")
            .also { require(it.isNotBlank()) }

        mockMvc.get("/api/v1/service-requests/$requestId/candidates") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $customerToken")
            param("radiusMeters", "5000")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].displayName") { value("Candidate Professional") }
        }

        assertThat(serviceRequestRepository.count()).isEqualTo(1)
    }

    @Test
    fun `requires authentication to create a service request`() {
        mockMvc.post("/api/v1/service-requests") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    private fun createAvailableProfessional(
        requestedAt: java.time.Instant,
        zone: ZoneId,
    ) {
        val credentials = credentials(
            "candidate.professional@example.com",
            "a-secure-password",
        )
        register(credentials)
        var token = login(credentials)

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "Candidate Professional",
                  "phone": "+34600123456"
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

        val localRequestedAt = requestedAt.atZone(zone)
        val dayOfWeek = localRequestedAt.dayOfWeek.name

        mockMvc.put("/api/v1/professionals/me/availability") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "timeZone": "Europe/Madrid",
                  "slots": [
                    {
                      "dayOfWeek": "$dayOfWeek",
                      "startTime": "09:00:00",
                      "endTime": "14:00:00"
                    }
                  ]
                }
            """.trimIndent()
        }.andExpect {
            status { isOk() }
        }
    }

    private fun registerAndLogin(email: String, password: String): String {
        val body = credentials(email, password)
        register(body)
        return login(body)
    }

    private fun register(body: String) {
        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isCreated() }
        }
    }

    private fun login(body: String): String {
        val response = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        return response
            .substringAfter("\"accessToken\":\"")
            .substringBefore("\"")
            .also { require(it.isNotBlank()) }
    }

    private fun credentials(email: String, password: String) = """
        {
          "email": "$email",
          "password": "$password"
        }
    """.trimIndent()
}
