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
class ProfessionalLocationSearchIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun `finds a nearby professional by category and orders by distance`() {
        val token = createProfessionalWithCategory()

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

        mockMvc.get("/api/v1/professionals/search") {
            param("latitude", "40.4170")
            param("longitude", "-3.7040")
            param("radiusMeters", "5000")
            param("category", "plumbing")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].displayName") { value("Madrid Professional") }
            jsonPath("$[0].distanceMeters") { value(org.hamcrest.Matchers.lessThan(50)) }
        }
    }

    @Test
    fun `does not return professionals outside the requested radius`() {
        val token = createProfessionalWithCategory()

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

        mockMvc.get("/api/v1/professionals/search") {
            param("latitude", "40.5000")
            param("longitude", "-3.7038")
            param("radiusMeters", "1000")
            param("category", "PLUMBING")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(0) }
        }
    }

    private fun createProfessionalWithCategory(): String {
        val credentials = """
            {
              "email": "geo.professional@example.com",
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
                  "displayName": "Madrid Professional",
                  "phone": "+34600555444"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
        }

        token = login(credentials)

        mockMvc.put("/api/v1/professionals/me/categories") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "categoryCodes": ["PLUMBING"]
                }
            """.trimIndent()
        }.andExpect {
            status { isOk() }
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
