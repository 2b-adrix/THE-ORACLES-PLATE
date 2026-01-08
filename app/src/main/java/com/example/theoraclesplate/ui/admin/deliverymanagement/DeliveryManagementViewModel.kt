package com.example.theoraclesplate.ui.admin.deliverymanagement

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var getDeliveryUsersJob: Job? = null

    init {
        getDeliveryUsers()
    }

    fun onEvent(event: DeliveryManagementEvent) {
        when (event) {
            is DeliveryManagementEvent.DeleteUser -> {
                viewModelScope.launch {
                    adminUseCases.deleteUser(event.userId)
                }
            }
        }
    }

    private fun getDeliveryUsers() {
        getDeliveryUsersJob?.cancel()
        getDeliveryUsersJob = adminUseCases.getDeliveryUsers()
            .onEach { users ->
                _state.value = state.value.copy(deliveryUsers = users)
            }
            .launchIn(viewModelScope)
    }
}

data class DeliveryManagementState(
    val deliveryUsers: List<Pair<String, User>> = emptyList()
)

sealed class DeliveryManagementEvent {
    data class DeleteUser(val userId: String) : DeliveryManagementEvent()
}
