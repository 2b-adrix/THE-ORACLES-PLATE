package com.example.theoraclesplate.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.HomeUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var getBannersJob: Job? = null
    private var getPopularFoodJob: Job? = null

    init {
        getBanners()
        getPopularFood()
    }

    private fun getBanners() {
        getBannersJob?.cancel()
        getBannersJob = homeUseCases.getBanners()
            .onEach { result ->
                result.fold(
                    onSuccess = { banners ->
                        _state.value = state.value.copy(banners = banners, isLoading = false)
                    },
                    onFailure = { error ->
                        _state.value = state.value.copy(error = error.message, isLoading = false)
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    private fun getPopularFood() {
        getPopularFoodJob?.cancel()
        getPopularFoodJob = homeUseCases.getPopularFood()
            .onEach { result ->
                result.fold(
                    onSuccess = { popularFood ->
                        _state.value = state.value.copy(popularFood = popularFood, isLoading = false)
                    },
                    onFailure = { error ->
                        _state.value = state.value.copy(error = error.message, isLoading = false)
                    }
                )
            }
            .launchIn(viewModelScope)
    }
}

data class HomeState(
    val banners: List<String> = emptyList(),
    val popularFood: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
