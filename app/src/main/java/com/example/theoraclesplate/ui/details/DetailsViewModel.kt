package com.example.theoraclesplate.ui.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AuthUseCases
import com.example.theoraclesplate.domain.use_case.CartUseCases
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val cartUseCases: CartUseCases,
    private val authUseCases: AuthUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(DetailsState())
    val state: State<DetailsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        val name = savedStateHandle.get<String>("name")?.let {
            URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
        }
        val price = savedStateHandle.get<Float>("price")
        val image = savedStateHandle.get<String>("image")?.let {
            URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
        }

        if (name != null && price != null) {
            _state.value = state.value.copy(
                foodItem = FoodItem(
                    name = name,
                    price = price.toDouble(),
                    imageUrl = image ?: ""
                ),
                isLoading = false
            )
        }
    }

    fun onEvent(event: DetailsEvent) {
        when(event) {
            is DetailsEvent.AddToCart -> {
                viewModelScope.launch {
                    val userId = authUseCases.getCurrentUser()?.uid
                    if (userId != null) {
                        val cartItem = CartItem(
                            id = event.item.name, // Using name as ID
                            name = event.item.name,
                            price = event.item.price,
                            image = event.item.imageUrl,
                            quantity = 1,
                            sellerId = event.item.sellerId
                        )
                        cartUseCases.addToCart(userId, cartItem)
                        _eventFlow.emit(UiEvent.ShowToast("Added to cart"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}

data class DetailsState(
    val foodItem: FoodItem? = null,
    val isLoading: Boolean = true
)

sealed class DetailsEvent {
    data class AddToCart(val item: FoodItem): DetailsEvent()
}
