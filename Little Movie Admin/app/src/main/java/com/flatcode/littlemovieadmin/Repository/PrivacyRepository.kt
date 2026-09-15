package com.flatcode.littlemovieadmin.Repository

import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivacyRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun getPrivacyPolicy(): String =
        database.getReference(DATA.TOOLS).child(DATA.PRIVACY_POLICY).get().await().value?.toString() ?: ""

    suspend fun updatePrivacyPolicy(content: String) =
        database.getReference(DATA.TOOLS).child(DATA.PRIVACY_POLICY).setValue(content).await()
}
