package com.localnow.identity.api

data class ApiError(
    val code: String,
    val message: String,
    val fieldErrors: List<FieldValidationError> = emptyList(),
)

data class FieldValidationError(
    val field: String,
    val message: String,
)
