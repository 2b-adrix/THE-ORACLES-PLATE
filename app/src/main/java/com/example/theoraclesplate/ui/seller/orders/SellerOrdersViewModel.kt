package com.example.theoraclesplate.ui.seller.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.seller.SellerOrdersRepository
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SellerOrdersViewModel @Inject constructor(
    private val repository: SellerOrdersRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _todaysRevenue = MutableStateFlow(0.0)
    val todaysRevenue: StateFlow<Double> = _todaysRevenue.asStateFlow()

    private val _todaysOrders = MutableStateFlow(0)
    val todaysOrders: StateFlow<Int> = _todaysOrders.asStateFlow()

    private val _weeklySales = MutableStateFlow<Map<String, Double>>(emptyMap())
    val weeklySales: StateFlow<Map<String, Double>> = _weeklySales.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            repository.getOrders()
                .catch { e -> _error.value = e.message ?: "An unknown error occurred" }
                .collect { fetchedOrders ->
                    _orders.value = fetchedOrders
                    calculateTodaysMetrics(fetchedOrders)
                    calculateWeeklySales(fetchedOrders)
                }
        }
    }

    private fun calculateTodaysMetrics(orders: List<Order>) {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        val todaysFilteredOrders = orders.filter { order ->
            calendar.timeInMillis = order.timestamp
            calendar.get(Calendar.DAY_OF_YEAR) == today && calendar.get(Calendar.YEAR) == currentYear
        }

        _todaysRevenue.value = todaysFilteredOrders.sumOf { it.totalAmount }
        _todaysOrders.value = todaysFilteredOrders.size
    }

    private fun calculateWeeklySales(orders: List<Order>) {
        val calendar = Calendar.getInstance()
        val weeklySalesMap = mutableMapOf<String, Double>()
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        for (i in 0..6) {
            val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - i - 1).let {
                if (it < 0) it + 7 else it
            }
            val dayName = days[dayOfWeek]
            val dayStart = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            val dayEnd = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val salesForDay = orders.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.totalAmount }
            weeklySalesMap[dayName] = salesForDay
        }
        _weeklySales.value = weeklySalesMap
    }


    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
            } catch (e: Exception) {
                _error.value = e.message ?: "Could not update order status"
            }
        }
    }
}
