package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase,
) {
    suspend fun getUserInfo(uid: String): User? =
        try {
            database.getReference(DATA.USERS).child(uid).get().await().getValue(User::class.java)
        } catch (_: Exception) {
            null
        }

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val ref = database.getReference(DATA.USERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting all users")
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Void? =
        database.getReference(DATA.USERS).child(uid).updateChildren(updates).await()
}
