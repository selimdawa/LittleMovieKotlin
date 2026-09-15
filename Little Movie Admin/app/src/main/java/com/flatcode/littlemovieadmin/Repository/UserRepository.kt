package com.flatcode.littlemovieadmin.Repository

import com.flatcode.littlemovieadmin.Model.User
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun getUserInfo(uid: String): User? =
        database.getReference(DATA.USERS).child(uid).get().await().getValue(User::class.java)

    suspend fun getAllUsers(): List<User> {
        val snapshot = database.getReference(DATA.USERS).get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }

    suspend fun updateProfile(uid: String, updates: Map<String, Any>) =
        database.getReference(DATA.USERS).child(uid).updateChildren(updates).await()
}
