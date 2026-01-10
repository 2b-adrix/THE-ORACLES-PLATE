package com.example.theoraclesplate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute = _navigationRoute.asStateFlow()

    fun checkUserStatus() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    val snapshot = database.child("users").child(currentUser.uid).get().await()
                    val user = snapshot.getValue(User::class.java)
                    _navigationRoute.value = when (user?.role) {
                        "seller" -> "seller_dashboard"
                        "admin" -> "admin_panel"
                        "driver" -> "delivery_dashboard"
                        else -> "home"
                    }
                } catch (e: Exception) {
                    _navigationRoute.value = "start"
                }
            } else {
                _navigationRoute.value = "start"
            }
        }
    }
}
