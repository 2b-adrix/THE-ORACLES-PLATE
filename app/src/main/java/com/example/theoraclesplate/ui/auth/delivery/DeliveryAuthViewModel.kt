package com.example.theoraclesplate.ui.auth.delivery

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
class DeliveryAuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(DeliveryAuthState())
    val state: State<DeliveryAuthState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: DeliveryAuthEvent) {
        when (event) {
            is DeliveryAuthEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is DeliveryAuthEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is DeliveryAuthEvent.EnteredName -> {
                _state.value = state.value.copy(name = event.value)
            }
            is DeliveryAuthEvent.Login -> {
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
            is DeliveryAuthEvent.Signup -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        authUseCases.signupUser(state.value.email, state.value.password, state.value.name, "delivery")
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.AuthSuccess)
                    } catch (e: Exception) {
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object AuthSuccess : UiEvent()
    }
}

data class DeliveryAuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class DeliveryAuthEvent {
    data class EnteredName(val value: String) : DeliveryAuthEvent()
    data class EnteredEmail(val value: String) : DeliveryAuthEvent()
    data class EnteredPassword(val value: String) : DeliveryAuthEvent()
    object Login : DeliveryAuthEvent()
    object Signup : DeliveryAuthEvent()
}
