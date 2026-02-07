package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMenuItemsViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AllMenuItemsState())
    val state: State<AllMenuItemsState> = _state

    init {
        getAllMenuItems()
    }

    fun onEvent(event: AllMenuItemsEvent) {
        when (event) {
            is AllMenuItemsEvent.DeleteMenuItem -> {
                viewModelScope.launch {
                    adminUseCases.deleteMenuItem(event.sellerId, event.menuItemId)
                    getAllMenuItems()
                }
            }
        }
    }

    private fun getAllMenuItems() {
        adminUseCases.getAllMenuItems().onEach { result ->
            _state.value = state.value.copy(
                isLoading = false,
                menuItems = result.getOrNull() ?: emptyList()
            )
        }.launchIn(viewModelScope)
    }
}

data class AllMenuItemsState(
    val isLoading: Boolean = true,
    val menuItems: List<Pair<String, FoodItem>> = emptyList()
)

sealed class AllMenuItemsEvent {
    data class DeleteMenuItem(val sellerId: String, val menuItemId: String) : AllMenuItemsEvent()
}
