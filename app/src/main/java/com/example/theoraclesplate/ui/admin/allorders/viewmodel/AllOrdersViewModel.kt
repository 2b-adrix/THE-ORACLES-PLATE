package com.example.theoraclesplate.ui.admin.allorders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AllOrdersState())
    val state = _state.asStateFlow()

    init {
        getAllOrders()
    }

    private fun getAllOrders() {
        viewModelScope.launch {
            adminUseCases.getAllOrders().collectLatest { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            orders = result.getOrNull() ?: emptyList(),
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

    fun onEvent(event: AllOrdersEvent) {
        viewModelScope.launch {
            when (event) {
                is AllOrdersEvent.DeleteOrder -> {
                    adminUseCases.deleteOrder(event.orderId)
                }
            }
        }
    }
}

data class AllOrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AllOrdersEvent {
    data class DeleteOrder(val orderId: String) : AllOrdersEvent()
}
