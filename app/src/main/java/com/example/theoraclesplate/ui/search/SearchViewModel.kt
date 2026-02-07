package com.example.theoraclesplate.ui.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(SearchState())
    val state: State<SearchState> = _state

    private var getMenuItemsJob: Job? = null

    init {
        getAllMenuItems()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _state.value = state.value.copy(query = event.query)
                filterItems()
            }
        }
    }

    private fun getAllMenuItems() {
        getMenuItemsJob?.cancel()
        getMenuItemsJob = adminUseCases.getAllMenuItems()
            .onEach { result ->
                val items = result.getOrNull()?.map { it.second } ?: emptyList()
                _state.value = state.value.copy(
                    menuItems = items,
                    isLoading = false
                )
                filterItems()
            }
            .launchIn(viewModelScope)
    }

    private fun filterItems() {
        val filtered = if (state.value.query.isBlank()) {
            state.value.menuItems
        } else {
            state.value.menuItems.filter {
                it.name.contains(state.value.query, ignoreCase = true)
            }
        }
        _state.value = state.value.copy(filteredItems = filtered)
    }
}

data class SearchState(
    val menuItems: List<FoodItem> = emptyList(),
    val filteredItems: List<FoodItem> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true
)

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
}
