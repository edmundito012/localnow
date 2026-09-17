package com.localnow.identity.api

import com.localnow.identity.application.EmailAlreadyRegisteredException
import com.localnow.identity.application.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class IdentityExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidCredentials() = ApiError(
        code = "INVALID_CREDENTIALS",
        message = "Email or password is incorrect",
    )

    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleEmailAlreadyRegistered(exception: EmailAlreadyRegisteredException) = ApiError(
        code = "EMAIL_ALREADY_REGISTERED",
        message = requireNotNull(exception.message),
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(exception: MethodArgumentNotValidException) = ApiError(
        code = "VALIDATION_FAILED",
        message = "Request validation failed",
        fieldErrors = exception.bindingResult.fieldErrors.map { error ->
            FieldValidationError(
                field = error.field,
                message = error.defaultMessage ?: "Invalid value",
            )
        },
    )

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(exception: IllegalArgumentException) = ApiError(
        code = "INVALID_REGISTRATION",
        message = exception.message ?: "Registration is invalid",
    )
}
