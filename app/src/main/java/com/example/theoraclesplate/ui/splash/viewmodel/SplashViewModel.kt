package com.example.theoraclesplate.ui.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theoraclesplate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute = _navigationRoute.asStateFlow()

    fun checkUserStatus() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    val snapshot = database.reference.child("users").child(currentUser.uid).get().await()
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
