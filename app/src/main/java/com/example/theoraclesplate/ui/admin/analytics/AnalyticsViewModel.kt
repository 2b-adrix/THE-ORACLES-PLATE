package com.example.theoraclesplate.ui.admin.analytics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.AnalyticsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AnalyticsState())
    val state: State<AnalyticsState> = _state

    private var getAnalyticsDataJob: Job? = null

    init {
        getAnalyticsData()
    }

    private fun getAnalyticsData() {
        getAnalyticsDataJob?.cancel()
        getAnalyticsDataJob = adminUseCases.getAnalyticsData()
            .onEach { data ->
                _state.value = state.value.copy(analyticsData = data)
            }
            .launchIn(viewModelScope)
    }
}

data class AnalyticsState(
    val analyticsData: AnalyticsData = AnalyticsData()
)
