package com.flatcode.littlemovieadmin.utils

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryHelper {

    suspend fun uploadFile(
        uri: Uri,
        isVideo: Boolean = false,
        onProgress: ((Int) -> Unit)? = null
    ): String = suspendCancellableCoroutine { continuation ->
        val request = MediaManager.get().upload(uri)
            .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)

        if (isVideo) {
            request.option("resource_type", "video")
        }

        request.callback(object : UploadCallback {
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                val progress = (100.0 * bytes / totalBytes).toInt()
                onProgress?.invoke(progress)
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val url = (resultData["secure_url"] as? String) ?: (resultData["url"] as String)
                continuation.resume(url)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                continuation.resumeWithException(Exception(error.description))
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }
}
