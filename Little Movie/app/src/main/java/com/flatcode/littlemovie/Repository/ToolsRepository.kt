package com.flatcode.littlemovie.Repository

import com.flatcode.littlemovie.Unit.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class ToolsRepository @Inject constructor() {

    private val database = FirebaseDatabase.getInstance()
    private val toolsRef = database.getReference(DATA.TOOLS)

    fun getPrivacyPolicy(): Flow<String?> = callbackFlow {
        val listener = toolsRef.child(DATA.PRIVACY_POLICY).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.value?.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching privacy policy: %s", error.message)
                close(error.toException())
            }
        })
        awaitClose { toolsRef.child(DATA.PRIVACY_POLICY).removeEventListener(listener) }
    }
}
