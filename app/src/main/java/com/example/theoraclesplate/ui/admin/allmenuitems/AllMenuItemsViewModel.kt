package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var getAllMenuItemsJob: Job? = null

    init {
        getAllMenuItems()
    }

    fun onEvent(event: AllMenuItemsEvent) {
        when (event) {
            is AllMenuItemsEvent.DeleteMenuItem -> {
                viewModelScope.launch {
                    adminUseCases.deleteMenuItem(event.menuItemId)
                }
            }
        }
    }

    private fun getAllMenuItems() {
        getAllMenuItemsJob?.cancel()
        getAllMenuItemsJob = adminUseCases.getAllMenuItems()
            .onEach { menuItems ->
                _state.value = state.value.copy(menuItems = menuItems)
            }
            .launchIn(viewModelScope)
    }
}

data class AllMenuItemsState(
    val menuItems: List<Pair<String, FoodItem>> = emptyList()
)

sealed class AllMenuItemsEvent {
    data class DeleteMenuItem(val menuItemId: String) : AllMenuItemsEvent()
}
