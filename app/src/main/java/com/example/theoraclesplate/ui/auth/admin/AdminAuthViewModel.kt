package com.example.theoraclesplate.ui.auth.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AdminAuthState())
    val state: State<AdminAuthState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: AdminAuthEvent) {
        when (event) {
            is AdminAuthEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is AdminAuthEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is AdminAuthEvent.Login -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        authUseCases.loginUser(state.value.email, state.value.password)
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.AuthSuccess)
                    } catch (e: Exception) {
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
                    }
                }
            }
            is AdminAuthEvent.Logout -> {
                viewModelScope.launch {
                    authUseCases.logoutUser()
                    _eventFlow.emit(UiEvent.LogoutSuccess)
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object AuthSuccess : UiEvent()
        object LogoutSuccess : UiEvent()
    }
}

data class AdminAuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class AdminAuthEvent {
    data class EnteredEmail(val value: String) : AdminAuthEvent()
    data class EnteredPassword(val value: String) : AdminAuthEvent()
    object Login : AdminAuthEvent()
    object Logout : AdminAuthEvent()
}
