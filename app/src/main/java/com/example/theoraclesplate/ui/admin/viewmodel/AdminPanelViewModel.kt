package com.example.theoraclesplate.ui.admin.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AdminPanelState())
    val state: State<AdminPanelState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getUsers()
    }

    fun onEvent(event: AdminPanelEvent) {
        when (event) {
            is AdminPanelEvent.ChangeRole -> {
                viewModelScope.launch {
                    try {
                        adminUseCases.updateUserRole(event.userId, event.newRole)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowToast(e.message ?: "Failed to update role"))
                    }
                }
            }
        }
    }

    private fun getUsers() {
        adminUseCases.getAllUsers().onEach { result ->
            result.fold(
                onSuccess = { users ->
                    _state.value = state.value.copy(
                        users = users,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    _eventFlow.emit(UiEvent.ShowToast(error.message ?: "Failed to load users"))
                }
            )
        }.launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}

data class AdminPanelState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true
)

sealed class AdminPanelEvent {
    data class ChangeRole(val userId: String, val newRole: String) : AdminPanelEvent()
}
