package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMenuItemsViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AllMenuItemsState())
    val state = _state.asStateFlow()

    init {
        getAllMenuItems()
    }

    private fun getAllMenuItems() {
        viewModelScope.launch {
            adminUseCases.getAllMenuItems().collectLatest { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            menuItems = result.getOrNull() ?: emptyList(),
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

    fun onEvent(event: AllMenuItemsEvent) {
        viewModelScope.launch {
            when (event) {
                is AllMenuItemsEvent.DeleteMenuItem -> {
                    adminUseCases.deleteMenuItem(event.sellerId, event.menuItemId)
                }
            }
        }
    }
}

data class AllMenuItemsState(
    val menuItems: List<Pair<String, FoodItem>> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AllMenuItemsEvent {
    data class DeleteMenuItem(val sellerId: String, val menuItemId: String) : AllMenuItemsEvent()
}
