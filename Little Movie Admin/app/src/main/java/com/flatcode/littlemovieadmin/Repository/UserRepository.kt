package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase,
) {
    suspend fun getUserInfo(uid: String): User? =
        database.getReference(DATA.USERS).child(uid).get().await().getValue(User::class.java)

    suspend fun getAllUsers(): List<User> {
        val snapshot = database.getReference(DATA.USERS).get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }

    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Void? =
        database.getReference(DATA.USERS).child(uid).updateChildren(updates).await()
}
