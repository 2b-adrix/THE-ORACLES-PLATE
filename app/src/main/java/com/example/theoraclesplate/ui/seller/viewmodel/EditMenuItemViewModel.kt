package com.example.theoraclesplate.ui.seller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMenuItemViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(EditMenuItemState())
    val state: State<EditMenuItemState> = _state

    private val menuItemId: String = savedStateHandle.get<String>("menuItemId") ?: ""

    init {
        getMenuItem()
    }

    private fun getMenuItem() {
        viewModelScope.launch {
            menuUseCases.getMenuItem(menuItemId, "").collectLatest { result ->
                result.fold(
                    onSuccess = { menuItem ->
                        _state.value = state.value.copy(
                            menuItem = menuItem,
                            name = menuItem?.name ?: "",
                            description = menuItem?.description ?: "",
                            price = menuItem?.price?.toString() ?: "",
                            imageUrl = menuItem?.imageUrl ?: ""
                        )
                    },
                    onFailure = {
                        // Handle error
                    }
                )
            }
        }
    }

    fun onEvent(event: EditMenuItemEvent) {
        when (event) {
            is EditMenuItemEvent.NameChanged -> _state.value = state.value.copy(name = event.name)
            is EditMenuItemEvent.DescriptionChanged -> _state.value = state.value.copy(description = event.description)
            is EditMenuItemEvent.PriceChanged -> _state.value = state.value.copy(price = event.price)
            is EditMenuItemEvent.ImageUrlChanged -> _state.value = state.value.copy(imageUrl = event.imageUrl)
            is EditMenuItemEvent.SaveChanges -> saveChanges()
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            val updatedMenuItem = state.value.menuItem?.copy(
                name = state.value.name,
                description = state.value.description,
                price = state.value.price.toDoubleOrNull() ?: 0.0,
                imageUrl = state.value.imageUrl
            )
            if (updatedMenuItem != null) {
                menuUseCases.updateMenuItem(menuItemId, "", updatedMenuItem)
            }
        }
    }
}

data class EditMenuItemState(
    val menuItem: FoodItem? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = ""
)

sealed class EditMenuItemEvent {
    data class NameChanged(val name: String) : EditMenuItemEvent()
    data class DescriptionChanged(val description: String) : EditMenuItemEvent()
    data class PriceChanged(val price: String) : EditMenuItemEvent()
    data class ImageUrlChanged(val imageUrl: String) : EditMenuItemEvent()
    object SaveChanges : EditMenuItemEvent()
}
