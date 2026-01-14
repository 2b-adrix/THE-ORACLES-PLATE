package com.example.theoraclesplate.ui.admin.allmenuitems

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMenuItemsViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AllMenuItemsState())
    val state: State<AllMenuItemsState> = _state

    private var getMenuItemsJob: Job? = null

    init {
        getMenuItems()
    }

    fun onEvent(event: AllMenuItemsEvent) {
        when (event) {
            is AllMenuItemsEvent.AddMenuItem -> {
                viewModelScope.launch {
                    menuUseCases.addMenuItem(event.item.sellerId, event.item)
                }
            }
            is AllMenuItemsEvent.UpdateMenuItem -> {
                viewModelScope.launch {
                    menuUseCases.updateMenuItem(event.item.sellerId, event.key, event.item)
                }
            }
            is AllMenuItemsEvent.DeleteMenuItem -> {
                viewModelScope.launch {
                    menuUseCases.deleteMenuItem(event.sellerId, event.menuItemId)
                }
            }
        }
    }

    private fun getMenuItems() {
        getMenuItemsJob?.cancel()
        getMenuItemsJob = menuUseCases.getMenuItems()
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
    data class AddMenuItem(val item: FoodItem) : AllMenuItemsEvent()
    data class UpdateMenuItem(val key: String, val item: FoodItem) : AllMenuItemsEvent()
    data class DeleteMenuItem(val sellerId: String, val menuItemId: String) : AllMenuItemsEvent()
}
