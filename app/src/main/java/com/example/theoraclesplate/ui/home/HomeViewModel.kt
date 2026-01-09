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
            .onEach { banners ->
                _state.value = state.value.copy(banners = banners)
            }
            .launchIn(viewModelScope)
    }

    private fun getPopularFood() {
        getPopularFoodJob?.cancel()
        getPopularFoodJob = homeUseCases.getPopularFood()
            .onEach { food ->
                _state.value = state.value.copy(popularFood = food)
            }
            .launchIn(viewModelScope)
    }
}

data class HomeState(
    val banners: List<Int> = emptyList(),
    val popularFood: List<FoodItem> = emptyList()
)
