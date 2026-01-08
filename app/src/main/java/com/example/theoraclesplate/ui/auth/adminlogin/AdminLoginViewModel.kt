package com.example.theoraclesplate.ui.auth.adminlogin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLoginViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(AdminLoginState())
    val state: State<AdminLoginState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: AdminLoginEvent) {
        when (event) {
            is AdminLoginEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is AdminLoginEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is AdminLoginEvent.Login -> {
                viewModelScope.launch {
                    // In a real app, this would involve a proper authentication call
                    // For now, we are keeping the hardcoded check
                    if (state.value.email == "admin@gmail.com" && state.value.password == "admin123") {
                        _eventFlow.emit(UiEvent.LoginSuccess)
                    } else {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Invalid admin credentials"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object LoginSuccess : UiEvent()
    }
}

data class AdminLoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class AdminLoginEvent {
    data class EnteredEmail(val value: String) : AdminLoginEvent()
    data class EnteredPassword(val value: String) : AdminLoginEvent()
    object Login : AdminLoginEvent()
}
