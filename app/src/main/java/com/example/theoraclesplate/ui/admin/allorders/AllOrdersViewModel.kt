package com.example.theoraclesplate.ui.admin.allorders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AllOrdersState())
    val state: State<AllOrdersState> = _state

    private var getAllOrdersJob: Job? = null

    init {
        getAllOrders()
    }

    fun onEvent(event: AllOrdersEvent) {
        when (event) {
            is AllOrdersEvent.DeleteOrder -> {
                viewModelScope.launch {
                    adminUseCases.deleteOrder(event.orderId)
                }
            }
        }
    }

    private fun getAllOrders() {
        getAllOrdersJob?.cancel()
        getAllOrdersJob = adminUseCases.getAllOrders()
            .onEach { orders ->
                _state.value = state.value.copy(orders = orders)
            }
            .launchIn(viewModelScope)
    }
}

data class AllOrdersState(
    val orders: List<Pair<String, Order>> = emptyList()
)

sealed class AllOrdersEvent {
    data class DeleteOrder(val orderId: String) : AllOrdersEvent()
}
