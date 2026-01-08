package com.example.theoraclesplate.ui.auth.signup

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
class SignUpViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SignUpState())
    val state: State<SignUpState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is SignUpEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is SignUpEvent.EnteredName -> {
                _state.value = state.value.copy(name = event.value)
            }
            is SignUpEvent.SignUp -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.signupUser(state.value.email, state.value.password)
                        .onEach { result ->
                            _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                _eventFlow.emit(UiEvent.SignUpSuccess)
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
        object SignUpSuccess : UiEvent()
    }
}

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class SignUpEvent {
    data class EnteredName(val value: String) : SignUpEvent()
    data class EnteredEmail(val value: String) : SignUpEvent()
    data class EnteredPassword(val value: String) : SignUpEvent()
    object SignUp : SignUpEvent()
}
