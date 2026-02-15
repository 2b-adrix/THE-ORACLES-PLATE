package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.repository.AdminRepository
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingSellersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PendingSellersState())
    val state = _state.asStateFlow()

    init {
        getPendingSellers()
    }

    private fun getPendingSellers() {
        viewModelScope.launch {
            adminRepository.getPendingSellers().collectLatest { result ->
                _state.value = when {
                    result.isSuccess -> {
                        state.value.copy(
                            pendingSellers = result.getOrNull() ?: emptyList(),
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

    fun onEvent(event: PendingSellersEvent) {
        viewModelScope.launch {
            when (event) {
                is PendingSellersEvent.ApproveSeller -> {
                    adminRepository.approveSeller(event.userId)
                }
                is PendingSellersEvent.DeclineSeller -> {
                    adminRepository.declineSeller(event.userId)
                }
            }
        }
    }
}

data class PendingSellersState(
    val pendingSellers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PendingSellersEvent {
    data class ApproveSeller(val userId: String) : PendingSellersEvent()
    data class DeclineSeller(val userId: String) : PendingSellersEvent()
}
