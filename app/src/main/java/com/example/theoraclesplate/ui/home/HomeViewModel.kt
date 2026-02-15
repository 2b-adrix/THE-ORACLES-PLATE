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
                _state.value = when {
                    result.isSuccess -> state.value.copy(banners = result.getOrNull() ?: emptyList(), isLoading = false)
                    result.isFailure -> state.value.copy(error = result.exceptionOrNull()?.message, isLoading = false)
                    else -> state.value.copy(isLoading = true)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getPopularFood() {
        getPopularFoodJob?.cancel()
        getPopularFoodJob = homeUseCases.getPopularFood()
            .onEach { result ->
                _state.value = when {
                    result.isSuccess -> state.value.copy(popularFood = result.getOrNull() ?: emptyList(), isLoading = false)
                    result.isFailure -> state.value.copy(error = result.exceptionOrNull()?.message, isLoading = false)
                    else -> state.value.copy(isLoading = true)
                }
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
