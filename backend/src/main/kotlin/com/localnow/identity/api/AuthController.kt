package com.localnow.identity.api

import com.localnow.identity.application.AuthenticateUserCommand
import com.localnow.identity.application.AuthenticateUserService
import com.localnow.identity.application.RegisterUserCommand
import com.localnow.identity.application.RegisterUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerUserService: RegisterUserService,
    private val authenticateUserService: AuthenticateUserService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterUserRequest): RegisterUserResponse {
        val userId = registerUserService.register(
            RegisterUserCommand(
                email = request.email,
                rawPassword = request.password,
            ),
        )

        return RegisterUserResponse(userId)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {
        val authenticatedUser = authenticateUserService.authenticate(
            AuthenticateUserCommand(
                email = request.email,
                rawPassword = request.password,
            ),
        )

        return LoginResponse(
            userId = authenticatedUser.userId,
            email = authenticatedUser.email,
            roles = authenticatedUser.roles,
        )
    }
}
