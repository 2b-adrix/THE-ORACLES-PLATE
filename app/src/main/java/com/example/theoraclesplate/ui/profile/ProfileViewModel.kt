package com.example.theoraclesplate.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        _state.value = ProfileState(user = authRepository.getCurrentUser())
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Logout -> {
                viewModelScope.launch {
                    authRepository.logout()
                    _eventChannel.send(UiEvent.Logout)
                }
            }
        }
    }

    sealed class UiEvent {
        object Logout : UiEvent()
    }
}

data class ProfileState(val user: FirebaseUser? = null)

sealed class ProfileEvent {
    object Logout : ProfileEvent()
}
