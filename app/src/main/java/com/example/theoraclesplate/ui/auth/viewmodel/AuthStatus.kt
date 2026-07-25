package com.example.theoraclesplate.ui.auth.viewmodel

sealed class AuthStatus {
    object Empty : AuthStatus()
    object Loading : AuthStatus()
    data class Success(val message: String) : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}
