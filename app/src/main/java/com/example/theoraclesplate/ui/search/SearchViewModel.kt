package com.example.theoraclesplate.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.SearchRepository
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(500)
            .onEach { query ->
                searchMenuItems(query)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.value = state.value.copy(searchQuery = query)
    }

    private fun searchMenuItems(query: String) {
        searchRepository.searchMenuItems(query)
            .onEach { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            searchResults = result.getOrNull() ?: emptyList(),
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
            .launchIn(viewModelScope)
    }
}

data class SearchState(
    val searchQuery: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
