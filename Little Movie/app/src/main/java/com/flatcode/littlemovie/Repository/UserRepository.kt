package com.flatcode.littlemovie.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemovie.db.UserDao
import com.flatcode.littlemovie.model.User
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class UserRepository @Inject constructor(
    private val userDao: UserDao,
) {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference(DATA.USERS)
    private val interestedRef = database.getReference(DATA.INTERESTED)
    private val favoritesRef = database.getReference(DATA.FAVORITES)

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserProfileImage(userId: String): Flow<String?> = callbackFlow {
        Timber.d("Fetching profile image for user: %s", userId)
        val listener = usersRef.child(userId).child(DATA.PROFILE_IMAGE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.value?.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Error fetching profile image: %s", error.message)
                    close(error.toException())
                }
            })
        awaitClose {
            usersRef.child(userId).child(DATA.PROFILE_IMAGE).removeEventListener(listener)
        }
    }

    fun getUserInfo(userId: String): Flow<User?> = callbackFlow {
        val listener = usersRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    launch {
                        userDao.insertUser(it)
                    }
                }
                trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { usersRef.child(userId).removeEventListener(listener) }
    }

    suspend fun updateProfile(userId: String, username: String, imageUrl: String?): Result<Unit> {
        return try {
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.USER_NAME] = username
            if (imageUrl != null) {
                hashMap[DATA.PROFILE_IMAGE] = imageUrl
            }
            usersRef.child(userId).updateChildren(hashMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(
        userId: String, imageUri: Uri, extension: String,
    ): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            val builder = MediaManager.get().upload(imageUri)
                .option("folder", "Images/Profile")
                .option("public_id", userId)
                .option("unsigned", true)
                .option("upload_preset", DATA.CLOUDINARY_UPLOAD_PRESET)

            if (extension.isNotBlank()) {
                builder.option("format", extension)
            }

            builder.callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Timber.d("Cloudinary upload started: %s", requestId)
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Progress can be handled here if needed
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        continuation.resume(Result.success(url))
                    } else {
                        continuation.resume(Result.failure(Exception("Failed to get secure URL from Cloudinary")))
                    }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Timber.e("Cloudinary upload error: %s", error.description)
                    continuation.resume(Result.failure(Exception(error.description)))
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Timber.w("Cloudinary upload rescheduled: %s", error.description)
                }
            }).dispatch()
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Failed to get user UID")

            val hashMap = HashMap<String, Any>()
            hashMap[DATA.EMAIL] = email
            hashMap[DATA.ID] = userId
            hashMap[DATA.PROFILE_IMAGE] = DATA.BASIC
            hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
            hashMap[DATA.USER_NAME] = name
            hashMap[DATA.VERSION] = DATA.CURRENT_VERSION

            usersRef.child(userId).setValue(hashMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getInterestedCount(userId: String, type: String): Flow<Int> = callbackFlow {
        val listener = interestedRef.child(userId).child(type)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { interestedRef.child(userId).child(type).removeEventListener(listener) }
    }

    fun getFavoritesCount(userId: String): Flow<Int> = callbackFlow {
        val listener =
            favoritesRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { favoritesRef.child(userId).removeEventListener(listener) }
    }

    fun getInterestedCategories(userId: String): Flow<List<String>> = callbackFlow {
        val listener = interestedRef.child(userId).child(DATA.CATEGORIES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    for (data in snapshot.children) {
                        data.key?.let { list.add(it) }
                    }
                    trySend(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose {
            interestedRef.child(userId).child(DATA.CATEGORIES).removeEventListener(listener)
        }
    }

    fun isInterested(userId: String, type: String, id: String): Flow<Boolean> = callbackFlow {
        val listener = interestedRef.child(userId).child(type).child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose {
            interestedRef.child(userId).child(type).child(id).removeEventListener(listener)
        }
    }

    suspend fun toggleInterest(userId: String, type: String, id: String, isInterested: Boolean) {
        try {
            if (isInterested) {
                incrementInterestedCount(id, type, 1)
                interestedRef.child(userId).child(type).child(id).setValue(true).await()
            } else {
                incrementInterestedCount(id, type, -1)
                interestedRef.child(userId).child(type).child(id).removeValue().await()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error toggling interest")
        }
    }

    private suspend fun incrementInterestedCount(id: String, type: String, increment: Long) {
        try {
            val ref = database.getReference(type).child(id).child(DATA.INTERESTED_COUNT)
            val snapshot = ref.get().await()
            val currentCount = snapshot.getValue(Long::class.java) ?: 0L
            ref.setValue(currentCount + increment).await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating interested count")
        }
    }
}
