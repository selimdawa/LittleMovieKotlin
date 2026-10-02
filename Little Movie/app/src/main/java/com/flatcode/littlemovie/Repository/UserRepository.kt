package com.flatcode.littlemovie.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemovie.db.FavoriteDao
import com.flatcode.littlemovie.db.InterestedDao
import com.flatcode.littlemovie.db.UserDao
import com.flatcode.littlemovie.model.FavoriteEntity
import com.flatcode.littlemovie.model.InterestedEntity
import com.flatcode.littlemovie.model.User
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val favoriteDao: FavoriteDao,
    private val interestedDao: InterestedDao,
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

    fun getUserInfo(userId: String): Flow<User?> {
        syncUser(userId)
        return userDao.getUserById(userId)
    }

    private fun syncUser(userId: String) {
        if (userId.isEmpty()) return
        usersRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(User::class.java)?.let { user ->
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error syncing user info: %s", error.message)
            }
        })
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
        userId: String, imageUri: Uri,
    ): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            val publicId = "${userId}_${System.currentTimeMillis()}"
            MediaManager.get().upload(imageUri)
                .option("public_id", publicId)
                .option("folder", "Images/Profile")
                .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Timber.d("Cloudinary upload started: %s", requestId)
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            continuation.resume(Result.success(url))
                        } else {
                            continuation.resume(Result.failure(Exception("Failed to get secure URL from Cloudinary")))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        val description = error?.description ?: "Unknown error"
                        Timber.e("Cloudinary upload error: %s", description)
                        continuation.resume(Result.failure(Exception(description)))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
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

    fun getInterestedCount(userId: String, type: String): Flow<Int> {
        syncInterested(userId, type)
        return interestedDao.getInterestedCount(userId, type)
    }

    fun getFavoritesCount(userId: String): Flow<Int> {
        syncFavorites(userId)
        return favoriteDao.getTotalFavoriteCount(userId)
    }

    private fun syncInterested(userId: String, type: String) {
        if (userId.isEmpty()) return
        interestedRef.child(userId).child(type)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, type, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        interestedDao.deleteAllInterestedForUser(userId, type)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Error syncing interested count for %s: %s", type, error.message)
                }
            })
    }

    private fun syncFavorites(userId: String) {
        if (userId.isEmpty()) return
        favoritesRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favList = snapshot.children.mapNotNull { it.key }
                    .map { FavoriteEntity(userId, it) }
                CoroutineScope(Dispatchers.IO).launch {
                    favoriteDao.deleteAllFavoritesForUser(userId)
                    favoriteDao.insertFavorites(favList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error syncing favorites for user %s: %s", userId, error.message)
            }
        })
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
                interestedDao.insertInterested(InterestedEntity(userId, type, id))
            } else {
                incrementInterestedCount(id, type, -1)
                interestedRef.child(userId).child(type).child(id).removeValue().await()
                interestedDao.deleteInterested(userId, type, id)
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
