package com.example.theoraclesplate.ui.seller.menu

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMenuViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SellerMenuState())
    val state: State<SellerMenuState> = _state

    init {
        getMenuItems()
    }

    fun onEvent(event: SellerMenuEvent) {
        when (event) {
            is SellerMenuEvent.AddItem -> {
                viewModelScope.launch {
                    authUseCases.getCurrentUser()?.uid?.let {
                        menuUseCases.addMenuItem(it, event.item)
                    }
                }
            }
            is SellerMenuEvent.UpdateItem -> {
                viewModelScope.launch {
                    authUseCases.getCurrentUser()?.uid?.let {
                        menuUseCases.updateMenuItem(it, event.itemId, event.item)
                    }
                }
            }
            is SellerMenuEvent.DeleteItem -> {
                viewModelScope.launch {
                    authUseCases.getCurrentUser()?.uid?.let {
                        menuUseCases.deleteMenuItem(it, event.itemId)
                    }
                }
            }
        }
    }

    private fun getMenuItems() {
        authUseCases.getCurrentUser()?.uid?.let { sellerId ->
            menuUseCases.getMyMenuItems(sellerId).onEach { items ->
                _state.value = state.value.copy(menuItems = items)
            }.launchIn(viewModelScope)
        }
    }
}

data class SellerMenuState(
    val menuItems: List<Pair<String, FoodItem>> = emptyList(),
    val isLoading: Boolean = false
)

sealed class SellerMenuEvent {
    data class AddItem(val item: FoodItem) : SellerMenuEvent()
    data class UpdateItem(val itemId: String, val item: FoodItem) : SellerMenuEvent()
    data class DeleteItem(val itemId: String) : SellerMenuEvent()
}
