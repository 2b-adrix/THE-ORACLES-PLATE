package com.example.theoraclesplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.HistoryUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCases: HistoryUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        getOrderHistory()
    }

    private fun getOrderHistory() {
        viewModelScope.launch {
            val userId = authUseCases.getCurrentUser()?.uid ?: return@launch
            historyUseCases.getOrderHistory(userId).collectLatest { orders ->
                _state.value = _state.value.copy(orders = orders)
            }
        }
    }
}

data class HistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
