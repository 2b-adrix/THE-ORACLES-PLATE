package com.example.theoraclesplate.ui.admin.pendingsellers

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.domain.use_case.AdminUseCases
import com.example.theoraclesplate.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var getPendingSellersJob: Job? = null

    init {
        getPendingSellers()
    }

    fun onEvent(event: PendingSellersEvent) {
        when (event) {
            is PendingSellersEvent.ApproveSeller -> {
                viewModelScope.launch {
                    adminUseCases.approveSeller(event.sellerId)
                }
            }
        }
    }

    private fun getPendingSellers() {
        getPendingSellersJob?.cancel()
        getPendingSellersJob = adminUseCases.getPendingSellers()
            .onEach { sellers ->
                _state.value = state.value.copy(pendingSellers = sellers)
            }
            .launchIn(viewModelScope)
    }
}

data class PendingSellersState(
    val pendingSellers: List<Pair<String, User>> = emptyList()
)

sealed class PendingSellersEvent {
    data class ApproveSeller(val sellerId: String) : PendingSellersEvent()
}
