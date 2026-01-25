package com.example.theoraclesplate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Empty)
    val loginState = _loginState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            // TODO: Implement actual login logic with repository
            _loginState.value = LoginState.Success("Logged in successfully!") // Placeholder
        }
    }

    fun signup(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            // TODO: Implement actual signup logic with repository
            _loginState.value = LoginState.Success("Signed up successfully!") // Placeholder
        }
    }
}

sealed class LoginState {
    object Empty : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
