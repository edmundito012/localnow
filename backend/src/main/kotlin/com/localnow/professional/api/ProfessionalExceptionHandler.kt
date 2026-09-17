package com.localnow.professional.api

import com.localnow.identity.api.ApiError
import com.localnow.professional.application.ProfessionalPhoneAlreadyRegisteredException
import com.localnow.professional.application.ProfessionalProfileAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ProfessionalExceptionHandler {

    @ExceptionHandler(ProfessionalProfileAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleProfileAlreadyExists(exception: ProfessionalProfileAlreadyExistsException) = ApiError(
        code = "PROFESSIONAL_PROFILE_ALREADY_EXISTS",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(ProfessionalPhoneAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handlePhoneAlreadyRegistered(exception: ProfessionalPhoneAlreadyRegisteredException) = ApiError(
        code = "PROFESSIONAL_PHONE_ALREADY_REGISTERED",
        message = requireNotNull(exception.message),
    )
}
