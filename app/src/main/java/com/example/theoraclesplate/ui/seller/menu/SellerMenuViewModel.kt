package com.example.theoraclesplate.ui.seller.menu

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.FoodItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMenuViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SellerMenuState())
    val state: State<SellerMenuState> = _state

    private var getMenuItemsJob: Job? = null

    private val currentUserId: String? = Firebase.auth.currentUser?.uid

    init {
        getMenuItems()
    }

    fun onEvent(event: SellerMenuEvent) {
        when (event) {
            is SellerMenuEvent.AddItem -> {
                viewModelScope.launch {
                    menuUseCases.addMenuItem(event.item.copy(sellerId = currentUserId ?: ""))
                }
            }
            is SellerMenuEvent.UpdateItem -> {
                viewModelScope.launch {
                    menuUseCases.updateMenuItem(event.key, event.item.copy(sellerId = currentUserId ?: ""))
                }
            }
            is SellerMenuEvent.DeleteItem -> {
                viewModelScope.launch {
                    menuUseCases.deleteMenuItem(event.key)
                }
            }
        }
    }

    private fun getMenuItems() {
        getMenuItemsJob?.cancel()
        currentUserId?.let {
            getMenuItemsJob = menuUseCases.getMyMenuItems(it)
                .onEach { items ->
                    _state.value = state.value.copy(menuItems = items)
                }
                .launchIn(viewModelScope)
        }
    }
}

data class SellerMenuState(
    val menuItems: List<Pair<String, FoodItem>> = emptyList()
)

sealed class SellerMenuEvent {
    data class AddItem(val item: FoodItem) : SellerMenuEvent()
    data class UpdateItem(val key: String, val item: FoodItem) : SellerMenuEvent()
    data class DeleteItem(val key: String) : SellerMenuEvent()
} 
