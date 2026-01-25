package com.example.theoraclesplate.ui.auth.seller

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
class SellerAuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SellerAuthState())
    val state: State<SellerAuthState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SellerAuthEvent) {
        when (event) {
            is SellerAuthEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is SellerAuthEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is SellerAuthEvent.EnteredName -> {
                _state.value = state.value.copy(name = event.value)
            }
            is SellerAuthEvent.Login -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.loginUser(state.value.email, state.value.password)
                        .onEach { result ->
                            _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                _eventFlow.emit(UiEvent.AuthSuccess)
                            }.onFailure {
                                _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Unknown error"))
                            }
                        }.launchIn(this)
                }
            }
            is SellerAuthEvent.Signup -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    authUseCases.signupUser(state.value.email, state.value.password, state.value.name, "seller_pending") // Changed role to seller_pending
                        .onEach { result ->
                            _state.value = state.value.copy(isLoading = false)
                            result.onSuccess {
                                _eventFlow.emit(UiEvent.AuthSuccess)
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
        object AuthSuccess : UiEvent()
    }
}

data class SellerAuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class SellerAuthEvent {
    data class EnteredName(val value: String) : SellerAuthEvent()
    data class EnteredEmail(val value: String) : SellerAuthEvent()
    data class EnteredPassword(val value: String) : SellerAuthEvent()
    object Login : SellerAuthEvent()
    object Signup : SellerAuthEvent()
}
