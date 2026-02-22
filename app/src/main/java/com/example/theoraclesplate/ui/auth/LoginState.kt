package com.example.theoraclesplate.ui.auth

sealed class LoginState {
    object Empty : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
