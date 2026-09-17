package com.localnow.job.api

import com.localnow.identity.api.ApiError
import com.localnow.job.application.JobNotFoundException
import com.localnow.job.application.ProfessionalNotEligibleException
import com.localnow.job.application.ServiceRequestAlreadyClaimedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class JobExceptionHandler {

    @ExceptionHandler(ProfessionalNotEligibleException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleNotEligible(exception: ProfessionalNotEligibleException) = ApiError(
        code = "PROFESSIONAL_NOT_ELIGIBLE",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(ServiceRequestAlreadyClaimedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyClaimed(exception: ServiceRequestAlreadyClaimedException) = ApiError(
        code = "SERVICE_REQUEST_ALREADY_CLAIMED",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(JobNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleJobNotFound(exception: JobNotFoundException) = ApiError(
        code = "JOB_NOT_FOUND",
        message = requireNotNull(exception.message),
    )
}
