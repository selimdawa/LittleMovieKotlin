package com.flatcode.littlemovieadmin.repository

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
class SliderRepository @Inject constructor(
    private val database: FirebaseDatabase,
) {
    fun getSliderImages(): Flow<Map<String, String>> = callbackFlow {
        val ref = database.getReference(DATA.SLIDER_SHOW)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = mutableMapOf<String, String>()
                for (i in 1..20) {
                    val url = snapshot.child(i.toString()).value?.toString() ?: ""
                    images[i.toString()] = url
                }
                trySend(images)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting slider images")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateSliderImage(name: String, imageUrl: String): Void? =
        database.getReference(DATA.SLIDER_SHOW).updateChildren(mapOf(name to imageUrl)).await()
}
