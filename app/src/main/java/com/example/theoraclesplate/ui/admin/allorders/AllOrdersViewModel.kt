package com.example.theoraclesplate.ui.admin.allorders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AllOrdersState())
    val state: State<AllOrdersState> = _state

    init {
        getAllOrders()
    }

    private fun getAllOrders() {
        adminUseCases.getAllOrders().onEach { result ->
            _state.value = state.value.copy(
                isLoading = false,
                orders = result.getOrNull() ?: emptyList()
            )
        }.launchIn(viewModelScope)
    }
}

data class AllOrdersState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList()
)
