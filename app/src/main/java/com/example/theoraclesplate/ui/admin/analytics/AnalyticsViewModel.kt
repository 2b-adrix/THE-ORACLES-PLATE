package com.example.theoraclesplate.ui.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state = _state.asStateFlow()

    init {
        getAnalyticsData()
    }

    private fun getAnalyticsData() {
        viewModelScope.launch {
            adminUseCases.getAnalyticsData().collectLatest { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            analyticsData = result.getOrNull() ?: emptyMap(),
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
            }
        }
    }
}

data class AnalyticsState(
    val analyticsData: Map<String, Any> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
