package com.example.theoraclesplate.ui.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is LoginEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is LoginEvent.EnteredName -> { // From SignUpViewModel
                _state.value = state.value.copy(name = event.value)
            }
            is LoginEvent.Login -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.loginUser(state.value.email, state.value.password)
                        .onEach { result ->
                            _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                val userId = authUseCases.getCurrentUser()?.uid
                                if (userId != null) {
                                    val role = authUseCases.getUserRole(userId) ?: "buyer"
                                    _eventFlow.emit(UiEvent.LoginSuccess(role))
                                } else {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("Could not get user ID"))
                                }
                            }.onFailure {
                                _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Unknown error"))
                            }
                        }.launchIn(this)
                }
            }
            is LoginEvent.LoginWithGoogle -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.loginWithGoogle(event.idToken)
                        .onEach { result ->
                            _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                val userId = authUseCases.getCurrentUser()?.uid
                                if (userId != null) {
                                    val role = authUseCases.getUserRole(userId) ?: "buyer"
                                    _eventFlow.emit(UiEvent.LoginSuccess(role))
                                } else {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("Could not get user ID"))
                                }
                            }.onFailure {
                                _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Unknown error"))
                            }
                        }.launchIn(this)
                }
            }
            is LoginEvent.Signup -> { // From SignUpViewModel
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.signupUser(state.value.email, state.value.password, state.value.name, "buyer")
                        .onEach { result ->
                             _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                _eventFlow.emit(UiEvent.SignupSuccess)
                            }.onFailure {
                                _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Unknown error"))
                            }
                        }.launchIn(this)
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class LoginSuccess(val role: String) : UiEvent()
        object SignupSuccess : UiEvent()
    }
}

data class LoginState(
    val name: String = "", // From SignUpState
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    data class EnteredEmail(val value: String) : LoginEvent()
    data class EnteredPassword(val value: String) : LoginEvent()
    data class EnteredName(val value: String) : LoginEvent() // From SignUpEvent
    object Login : LoginEvent()
    object Signup : LoginEvent()
    data class LoginWithGoogle(val idToken: String) : LoginEvent()
} 
