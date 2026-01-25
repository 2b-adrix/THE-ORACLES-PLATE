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

    private fun getAllUsers() {
        adminUseCases.getAllUsers().onEach { result ->
            _state.value = state.value.copy(
                isLoading = false,
                users = result.getOrNull() ?: emptyList()
            )
        }.launchIn(viewModelScope)
    }
}

data class AllUsersState(
    val isLoading: Boolean = true,
    val users: List<Pair<String, User>> = emptyList()
)
