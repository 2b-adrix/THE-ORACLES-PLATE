package com.example.theoraclesplate.ui.admin.deliverymanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryManagementViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(DeliveryManagementState())
    val state = _state.asStateFlow()

    init {
        getDeliveryUsers()
    }

    private fun getDeliveryUsers() {
        viewModelScope.launch {
            adminUseCases.getDeliveryUsers().collectLatest { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            deliveryUsers = result.getOrNull() ?: emptyList(),
                            isLoading = false
                        )
                    }
                    result.isFailure -> {
                        state.value.copy(
                            error = result.exceptionOrNull()?.message,
                            isLoading = false
                        )
                    }
                    else -> {
                        state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }
}

data class DeliveryManagementState(
    val deliveryUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
