package com.example.theoraclesplate.ui.delivery

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.DeliveryUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class DeliveryDashboardViewModel @Inject constructor(
    private val deliveryUseCases: DeliveryUseCases
) : ViewModel() {

    private val _state = mutableStateOf(DeliveryDashboardState())
    val state: State<DeliveryDashboardState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getReadyForPickupOrdersJob: Job? = null
    private var getOutForDeliveryOrdersJob: Job? = null
    private var getDeliveredOrdersJob: Job? = null

    init {
        getReadyForPickupOrders()
        getOutForDeliveryOrders()
        getDeliveredOrders()
    }

    fun onEvent(event: DeliveryDashboardEvent) {
        when (event) {
            is DeliveryDashboardEvent.UpdateOrderStatus -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    try {
                        deliveryUseCases.updateOrderStatus(event.orderId, event.newStatus)
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Order status updated!"))
                    } catch (e: Exception) {
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
                    }
                }
            }
        }
    }

    private fun getReadyForPickupOrders() {
        getReadyForPickupOrdersJob?.cancel()
        getReadyForPickupOrdersJob = deliveryUseCases.getReadyForPickupOrders()
            .onEach { orders ->
                val locations = orders.mapNotNull { deliveryUseCases.getCoordinatesFromAddress(it.address) }
                _state.value = state.value.copy(readyForPickupOrders = orders, orderLocations = locations)
            }
            .launchIn(viewModelScope)
    }

    private fun getOutForDeliveryOrders() {
        getOutForDeliveryOrdersJob?.cancel()
        getOutForDeliveryOrdersJob = deliveryUseCases.getOutForDeliveryOrders()
            .onEach { orders ->
                val locations = orders.mapNotNull { deliveryUseCases.getCoordinatesFromAddress(it.address) }
                _state.value = state.value.copy(outForDeliveryOrders = orders, orderLocations = locations)
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

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

data class DeliveryDashboardState(
    val readyForPickupOrders: List<Order> = emptyList(),
    val outForDeliveryOrders: List<Order> = emptyList(),
    val deliveredOrders: List<Order> = emptyList(),
    val orderLocations: List<GeoPoint> = emptyList(),
    val isLoading: Boolean = false
)

sealed class DeliveryDashboardEvent {
    data class UpdateOrderStatus(val orderId: String, val newStatus: String) : DeliveryDashboardEvent()
}
