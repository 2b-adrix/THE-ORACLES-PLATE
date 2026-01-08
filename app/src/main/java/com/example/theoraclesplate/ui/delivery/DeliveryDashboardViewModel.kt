package com.example.theoraclesplate.ui.delivery

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.DeliveryUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DeliveryDashboardViewModel @Inject constructor(
    private val deliveryUseCases: DeliveryUseCases
) : ViewModel() {

    private val _state = mutableStateOf(DeliveryDashboardState())
    val state: State<DeliveryDashboardState> = _state

    private var getReadyForPickupOrdersJob: Job? = null
    private var getOutForDeliveryOrdersJob: Job? = null
    private var getDeliveredOrdersJob: Job? = null

    init {
        getReadyForPickupOrders()
        getOutForDeliveryOrders()
        getDeliveredOrders()
    }

    private fun getReadyForPickupOrders() {
        getReadyForPickupOrdersJob?.cancel()
        getReadyForPickupOrdersJob = deliveryUseCases.getReadyForPickupOrders()
            .onEach { orders ->
                _state.value = state.value.copy(readyForPickupOrders = orders)
            }
            .launchIn(viewModelScope)
    }

    private fun getOutForDeliveryOrders() {
        getOutForDeliveryOrdersJob?.cancel()
        getOutForDeliveryOrdersJob = deliveryUseCases.getOutForDeliveryOrders()
            .onEach { orders ->
                _state.value = state.value.copy(outForDeliveryOrders = orders)
            }
            .launchIn(viewModelScope)
    }

    private fun getDeliveredOrders() {
        getDeliveredOrdersJob?.cancel()
        getDeliveredOrdersJob = deliveryUseCases.getDeliveredOrders()
            .onEach { orders ->
                _state.value = state.value.copy(deliveredOrders = orders)
            }
            .launchIn(viewModelScope)
    }
}

data class DeliveryDashboardState(
    val readyForPickupOrders: List<Order> = emptyList(),
    val outForDeliveryOrders: List<Order> = emptyList(),
    val deliveredOrders: List<Order> = emptyList()
)
