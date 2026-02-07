package com.example.theoraclesplate.ui.admin.analytics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AnalyticsState())
    val state: State<AnalyticsState> = _state

    init {
        getAnalyticsData()
    }

    private fun getAnalyticsData() {
        adminUseCases.getAnalyticsData().onEach { data ->
            _state.value = state.value.copy(
                analyticsData = data.getOrNull() ?: emptyMap(), 
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}

data class AnalyticsState(
    val analyticsData: Map<String, Any> = emptyMap(),
    val isLoading: Boolean = true
)
