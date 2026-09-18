package com.flatcode.littlemovieadmin.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun login(email: String, password: String) = 
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun sendPasswordResetEmail(email: String) = 
        auth.sendPasswordResetEmail(email).await()

    fun isUserLoggedIn() = auth.currentUser != null
    
    fun getCurrentUserUid() = auth.currentUser?.uid
}
