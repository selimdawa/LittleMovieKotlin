package com.flatcode.littlemovieadmin.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) {
    suspend fun login(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun sendPasswordResetEmail(email: String): Void? =
        auth.sendPasswordResetEmail(email).await()

    fun getCurrentUserUid(): String? = auth.currentUser?.uid
}