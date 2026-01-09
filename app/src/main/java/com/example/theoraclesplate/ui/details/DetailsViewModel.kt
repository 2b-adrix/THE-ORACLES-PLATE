package com.example.theoraclesplate.ui.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.DetailsUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val detailsUseCases: DetailsUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(DetailsState())
    val state: State<DetailsState> = _state

    init {
        savedStateHandle.get<String>("name")?.let {
            getFoodItemDetails(it)
        }
    }

    private fun getFoodItemDetails(foodItemName: String) {
        detailsUseCases.getFoodItemDetails(foodItemName).onEach { item ->
            _state.value = state.value.copy(foodItem = item, isLoading = false)
        }.launchIn(viewModelScope)
    }
}

data class DetailsState(
    val foodItem: FoodItem? = null,
    val isLoading: Boolean = true
)
