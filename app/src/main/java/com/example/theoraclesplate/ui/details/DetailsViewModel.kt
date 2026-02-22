package com.example.theoraclesplate.ui.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.AuthRepository
import com.example.theoraclesplate.domain.use_case.CartUseCases
import com.example.theoraclesplate.domain.use_case.MenuUseCases
import com.example.theoraclesplate.model.CartItem
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val menuUseCases: MenuUseCases,
    private val cartUseCases: CartUseCases,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(DetailsState())
    val state: State<DetailsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("foodItemId")?.let { foodItemId ->
            savedStateHandle.get<String>("sellerId")?.let { sellerId ->
                getMenuItem(foodItemId, sellerId)
            }
        }
    }

    private fun getMenuItem(foodItemId: String, sellerId: String) {
        menuUseCases.getMenuItem(foodItemId, sellerId).onEach { result ->
            _state.value = when {
                result.isSuccess -> {
                    state.value.copy(
                        foodItem = result.getOrNull(),
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
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.AddToCart -> {
                viewModelScope.launch {
                    state.value.foodItem?.let {
                        val userId = authRepository.getCurrentUser()?.uid
                        if (userId != null) {
                            cartUseCases.addToCart(
                                userId = userId,
                                cartItem = CartItem(
                                    id = it.id,
                                    name = it.name,
                                    price = it.price,
                                    imageUrl = it.imageUrl,
                                    sellerId = it.sellerId
                                )
                            )
                            _eventFlow.emit(UiEvent.ShowToast("Added to cart"))
                        } else {
                            _eventFlow.emit(UiEvent.ShowToast("Please login to add items to cart"))
                        }

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
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class DetailsEvent {
    data class AddToCart(val foodItem: FoodItem) : DetailsEvent()
}
