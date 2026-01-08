package com.example.theoraclesplate.domain.use_case

data class AuthUseCases(
    val loginUser: LoginUserUseCase,
    val signupUser: SignupUserUseCase,
    val logoutUser: LogoutUserUseCase,
    val getCurrentUser: GetCurrentUserUseCase
)
