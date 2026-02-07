package com.example.theoraclesplate.ui.admin.deliverymanagement

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryManagementViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(DeliveryManagementState())
    val state: State<DeliveryManagementState> = _state

    init {
        getDeliveryUsers()
    }

    fun onEvent(event: DeliveryManagementEvent) {
        when (event) {
            is DeliveryManagementEvent.DeleteUser -> {
                viewModelScope.launch {
                    adminUseCases.deleteUser(event.userId)
                    getDeliveryUsers()
                }
            }
        }
    }

    private fun getDeliveryUsers() {
        adminUseCases.getDeliveryUsers().onEach { result ->
            _state.value = state.value.copy(
                users = result.getOrNull() ?: emptyList(),
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}

data class DeliveryManagementState(
    val users: List<Pair<String, User>> = emptyList(),
    val isLoading: Boolean = true
)

sealed class DeliveryManagementEvent {
    data class DeleteUser(val userId: String) : DeliveryManagementEvent()
}
