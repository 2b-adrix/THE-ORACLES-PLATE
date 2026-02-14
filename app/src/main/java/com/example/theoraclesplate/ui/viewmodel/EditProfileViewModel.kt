package com.example.theoraclesplate.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth = Firebase.auth

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState = _updateState.asStateFlow()

    fun saveProfile(name: String, selectedImageUri: Uri?) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            if (selectedImageUri != null) {
                uploadImageAndupdateProfile(name, selectedImageUri)
            } else {
                updateUserProfile(name, auth.currentUser?.photoUrl)
            }
        }
    }

    private suspend fun uploadImageAndupdateProfile(name: String, imageUri: Uri) {
        val uploadedUrl = uploadImageToCloudinary(imageUri)
        if (uploadedUrl != null) {
            updateUserProfile(name, uploadedUrl)
        } else {
            _updateState.value = UpdateState.Error("Image upload failed: No URL returned.")
        }
    }

    private suspend fun uploadImageToCloudinary(imageUri: Uri): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .unsigned("unsigned_preset")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        Log.d("EditProfileViewModel", "Cloudinary success data: $resultData")
                        val url = (resultData["secure_url"] ?: resultData["url"]) as? String
                        continuation.resume(url?.let { Uri.parse(it) })
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        _updateState.value = UpdateState.Error("Image upload error: ${error.description}")
                        continuation.resume(null)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
    }

    private fun updateUserProfile(name: String, photoUri: Uri?) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(photoUri)
                    .build()

                user?.updateProfile(profileUpdates)?.await()
                _updateState.value = UpdateState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}
