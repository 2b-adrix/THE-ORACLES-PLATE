package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingSellersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(PendingSellersState())
    val state: State<PendingSellersState> = _state

    init {
        getPendingSellers()
    }

    private fun getPendingSellers() {
        _state.value = state.value.copy(isLoading = true)
        adminRepository.getPendingSellers().onEach { result ->
            _state.value = state.value.copy(
                isLoading = false,
                sellers = result.getOrNull() ?: emptyList()
            )
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: PendingSellersEvent) {
        when (event) {
            is PendingSellersEvent.ApproveSeller -> {
                viewModelScope.launch {
                    adminRepository.approveSeller(event.userId).onEach { result ->
                        result.onSuccess { getPendingSellers() } // Refresh the list
                    }.launchIn(this)
                }
            }
            is PendingSellersEvent.DeclineSeller -> {
                viewModelScope.launch {
                    adminRepository.declineSeller(event.userId).onEach { result ->
                        result.onSuccess { getPendingSellers() } // Refresh the list
                    }.launchIn(this)
                }
            }
        }
    }
}

data class PendingSellersState(
    val isLoading: Boolean = true,
    val sellers: List<Pair<String, User>> = emptyList()
)

sealed class PendingSellersEvent {
    data class ApproveSeller(val userId: String) : PendingSellersEvent()
    data class DeclineSeller(val userId: String) : PendingSellersEvent()
}
