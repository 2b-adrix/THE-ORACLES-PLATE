package com.example.theoraclesplate.ui.seller.menu

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.FoodItem
import com.example.theoraclesplate.service.ImageUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMenuViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases,
    private val authUseCases: AuthUseCases,
    private val imageUploader: ImageUploader
) : ViewModel() {

    private val _state = mutableStateOf(SellerMenuState())
    val state: State<SellerMenuState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getMenuItemsForCurrentUser()
    }

    private fun getMenuItemsForCurrentUser() {
        viewModelScope.launch {
            val sellerId = authUseCases.getCurrentUser()?.uid ?: return@launch
            _state.value = _state.value.copy(isLoading = true)
            menuUseCases.getMenuItems(sellerId).onEach { result ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    menuItems = result.getOrNull() ?: emptyList()
                )
            }.launchIn(viewModelScope)
        }
    }

    fun onEvent(event: SellerMenuEvent) {
        when (event) {
            is SellerMenuEvent.EnteredName -> {
                _state.value = state.value.copy(name = event.value)
            }
            is SellerMenuEvent.EnteredDescription -> {
                _state.value = state.value.copy(description = event.value)
            }
            is SellerMenuEvent.EnteredPrice -> {
                _state.value = state.value.copy(price = event.value)
            }
            is SellerMenuEvent.AddMenuItem -> {
                viewModelScope.launch {
                    val sellerId = authUseCases.getCurrentUser()?.uid
                    if (sellerId == null) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                        return@launch
                    }

                    _state.value = state.value.copy(isLoading = true)

                    val imageUrl = event.imageUri?.let { imageUploader.uploadImage(it).getOrNull() } ?: ""

                    val menuItem = FoodItem(
                        name = state.value.name,
                        description = state.value.description,
                        price = state.value.price.toDoubleOrNull() ?: 0.0,
                        sellerId = sellerId,
                        imageUrl = imageUrl
                    )
                    menuUseCases.addMenuItem(sellerId, menuItem).onEach { result ->
                        _state.value = state.value.copy(isLoading = false)
                        result.onSuccess {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Menu item added!"))
                            _eventFlow.emit(UiEvent.NavigateUp)
                            getMenuItemsForCurrentUser()
                        }.onFailure {
                            _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Error adding item"))
                        }
                    }.launchIn(this)
                }
            }
            is SellerMenuEvent.DeleteMenuItem -> {
                viewModelScope.launch {
                    val sellerId = authUseCases.getCurrentUser()?.uid
                    if (sellerId == null) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                        return@launch
                    }
                    menuUseCases.deleteMenuItem(sellerId, event.menuItemId).onEach { result ->
                        result.onSuccess {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Menu item deleted!"))
                            getMenuItemsForCurrentUser()
                        }.onFailure {
                            _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Error deleting item"))
                        }
                    }.launchIn(this)
                }
            }

            is SellerMenuEvent.EditMenuItem -> {
                viewModelScope.launch {
                    val sellerId = authUseCases.getCurrentUser()?.uid
                    if (sellerId == null) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                        return@launch
                    }
                    menuUseCases.updateMenuItem(sellerId, event.menuItemId, event.foodItem).onEach { result ->
                        result.onSuccess {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Menu item updated!"))
                            getMenuItemsForCurrentUser()
                        }.onFailure {
                            _eventFlow.emit(UiEvent.ShowSnackbar(it.message ?: "Error updating item"))
                        }
                    }.launchIn(this)
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateUp : UiEvent()
    }
}

data class SellerMenuState(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val menuItems: List<Pair<String, FoodItem>> = emptyList(),
    val isLoading: Boolean = false
)

sealed class SellerMenuEvent {
    data class EnteredName(val value: String) : SellerMenuEvent()
    data class EnteredDescription(val value: String) : SellerMenuEvent()
    data class EnteredPrice(val value: String) : SellerMenuEvent()
    data class AddMenuItem(val imageUri: Uri?) : SellerMenuEvent()
    data class EditMenuItem(val menuItemId: String, val foodItem: FoodItem) : SellerMenuEvent()
    data class DeleteMenuItem(val menuItemId: String) : SellerMenuEvent()
}
