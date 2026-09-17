package com.localnow.job.api

import com.localnow.TestcontainersConfiguration
import com.localnow.job.persistence.JobRepository
import com.localnow.servicerequest.domain.ServiceRequestStatus
import com.localnow.servicerequest.persistence.ServiceRequestRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class JobAcceptanceConcurrencyIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jobRepository: JobRepository,
    @Autowired private val serviceRequestRepository: ServiceRequestRepository,
) {

    @Test
    fun `only one professional can atomically accept an open request`() {
        val zone = ZoneId.of("Europe/Madrid")
        val requestedAt = ZonedDateTime.of(
            LocalDate.now(zone).plusDays(3),
            LocalTime.of(11, 0),
            zone,
        ).toInstant()

        val firstToken = createProfessional(
            email = "race.first@example.com",
            phone = "+34600101010",
            requestedAt = requestedAt,
            zone = zone,
        )
        val secondToken = createProfessional(
            email = "race.second@example.com",
            phone = "+34600202020",
            requestedAt = requestedAt,
            zone = zone,
        )
        val customerToken = registerAndLogin(
            email = "race.customer@example.com",
            password = "a-secure-password",
        )
        val requestId = createRequest(customerToken, requestedAt)

        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        try {
            val attempts = listOf(firstToken, secondToken).map { token ->
                executor.submit<Int> {
                    start.await(5, TimeUnit.SECONDS)
                    mockMvc.post("/api/v1/service-requests/$requestId/accept") {
                        header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    }.andReturn().response.status
                }
            }

            start.countDown()
            val statuses = attempts.map { it.get(15, TimeUnit.SECONDS) }

            assertThat(statuses).containsExactlyInAnyOrder(201, 409)
            assertThat(jobRepository.findAll()).hasSize(1)
            assertThat(
                serviceRequestRepository.findById(UUID.fromString(requestId)).orElseThrow().status,
            ).isEqualTo(ServiceRequestStatus.MATCHED)
        } finally {
            executor.shutdownNow()
        }
    }

    private fun createProfessional(
        email: String,
        phone: String,
        requestedAt: java.time.Instant,
        zone: ZoneId,
    ): String {
        val credentials = credentials(email, "a-secure-password")
        register(credentials)
        var token = login(credentials)

        mockMvc.post("/api/v1/professionals/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "displayName": "$email",
                  "phone": "$phone"
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

        val day = requestedAt.atZone(zone).dayOfWeek.name
        mockMvc.put("/api/v1/professionals/me/availability") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "timeZone": "Europe/Madrid",
                  "slots": [{
                    "dayOfWeek": "$day",
                    "startTime": "09:00:00",
                    "endTime": "14:00:00"
                  }]
                }
            """.trimIndent()
        }.andExpect {
            status { isOk() }
        }

        return token
    }

    private fun createRequest(customerToken: String, requestedAt: java.time.Instant): String {
        val body = mockMvc.post("/api/v1/service-requests") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $customerToken")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "categoryCode": "PLUMBING",
                  "description": "Concurrent acceptance test",
                  "latitude": 40.4170,
                  "longitude": -3.7040,
                  "requestedAt": "$requestedAt",
                  "budgetAmountCents": 12000,
                  "currency": "EUR"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        return body.substringAfter("\"id\":\"").substringBefore("\"")
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
