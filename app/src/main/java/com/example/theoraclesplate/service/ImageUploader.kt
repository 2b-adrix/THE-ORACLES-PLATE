package com.example.theoraclesplate.service

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ImageUploader {
    suspend fun uploadImage(uri: Uri): Result<String>
}

class CloudinaryImageUploader(private val context: Context) : ImageUploader {

    override suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            MediaManager.get().upload(uri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = (resultData["secure_url"] ?: resultData["url"]) as? String
                        if (url != null) {
                            continuation.resume(Result.success(url))
                        } else {
                            continuation.resume(Result.failure(Exception("Upload failed: URL not found in response.")))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception("Upload error: ${error.description}")))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // Reschedule logic can be implemented here if needed
                    }
                }).dispatch()
        }
    }
}
