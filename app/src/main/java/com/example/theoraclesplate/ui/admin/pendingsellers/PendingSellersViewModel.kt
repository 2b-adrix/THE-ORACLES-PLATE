package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingSellersViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(PendingSellersState())
    val state: State<PendingSellersState> = _state

    init {
        getPendingSellers()
    }

    fun onEvent(event: PendingSellersEvent) {
        when (event) {
            is PendingSellersEvent.ApproveSeller -> {
                viewModelScope.launch {
                    try {
                        adminUseCases.approveSeller(event.userId)
                        getPendingSellers()
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
            is PendingSellersEvent.DeclineSeller -> {
                viewModelScope.launch {
                    try {
                        adminUseCases.declineSeller(event.userId)
                        getPendingSellers()
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        }
    }

    private fun getPendingSellers() {
        adminUseCases.getPendingSellers().onEach { result ->
            _state.value = state.value.copy(
                sellers = result.getOrNull() ?: emptyList(),
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}

data class PendingSellersState(
    val sellers: List<Pair<String, User>> = emptyList(),
    val isLoading: Boolean = true
)

sealed class PendingSellersEvent {
    data class ApproveSeller(val userId: String) : PendingSellersEvent()
    data class DeclineSeller(val userId: String) : PendingSellersEvent()
}
