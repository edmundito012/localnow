package com.localnow.servicerequest.api

import com.localnow.identity.api.ApiError
import com.localnow.servicerequest.application.InvalidServiceRequestException
import com.localnow.servicerequest.application.ServiceCategoryUnavailableException
import com.localnow.servicerequest.application.ServiceRequestNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ServiceRequestExceptionHandler {

    @ExceptionHandler(InvalidServiceRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidRequest(exception: InvalidServiceRequestException) = ApiError(
        code = "INVALID_SERVICE_REQUEST",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(ServiceCategoryUnavailableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnavailableCategory(exception: ServiceCategoryUnavailableException) = ApiError(
        code = "SERVICE_CATEGORY_UNAVAILABLE",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(ServiceRequestNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: ServiceRequestNotFoundException) = ApiError(
        code = "SERVICE_REQUEST_NOT_FOUND",
        message = requireNotNull(exception.message),
    )
}
