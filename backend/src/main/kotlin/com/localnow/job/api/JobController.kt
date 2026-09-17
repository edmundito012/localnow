package com.localnow.job.api

import com.localnow.job.application.JobService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class JobController(
    private val jobService: JobService,
) {

    @PostMapping("/service-requests/{requestId}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    fun accept(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable requestId: UUID,
    ): JobResponse =
        JobResponse.from(
            jobService.accept(
                requestId = requestId,
                professionalUserId = UUID.fromString(jwt.subject),
            ),
        )

    @GetMapping("/jobs/{jobId}")
    fun get(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable jobId: UUID,
    ): JobResponse =
        JobResponse.from(
            jobService.get(
                jobId = jobId,
                authenticatedUserId = UUID.fromString(jwt.subject),
            ),
        )
}
