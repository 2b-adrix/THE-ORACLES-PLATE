package com.example.theoraclesplate.ui.admin.allusers

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
class AllUsersViewModel @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AllUsersState())
    val state: State<AllUsersState> = _state

    init {
        getAllUsers()
    }

    fun onEvent(event: AllUsersEvent) {
        when (event) {
            is AllUsersEvent.DeleteUser -> {
                viewModelScope.launch {
                    adminUseCases.deleteUser(event.userId)
                    getAllUsers()
                }
            }
        }
    }

    private fun getAllUsers() {
        adminUseCases.getAllUsers().onEach { result ->
            _state.value = state.value.copy(
                users = result.getOrNull() ?: emptyList(), 
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }
}

data class AllUsersState(
    val users: List<Pair<String, User>> = emptyList(),
    val isLoading: Boolean = true
)

sealed class AllUsersEvent {
    data class DeleteUser(val userId: String) : AllUsersEvent()
}
