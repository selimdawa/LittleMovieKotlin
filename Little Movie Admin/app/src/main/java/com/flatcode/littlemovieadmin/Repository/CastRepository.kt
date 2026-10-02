package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.Cast
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
class CastRepository @Inject constructor(
    database: FirebaseDatabase,
) {
    private val castRef = database.getReference(DATA.CAST)
    private val castMovieRef = database.getReference(DATA.CAST_MOVIE)

    fun getCastList(orderBy: String): Flow<List<Cast>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Cast::class.java) }
                val sorted = when (orderBy) {
                    DATA.NAME -> list.sortedBy { it.name }
                    DATA.MOVIES_COUNT -> list.sortedByDescending { it.moviesCount }
                    DATA.INTERESTED_COUNT -> list.sortedByDescending { it.interestedCount }
                    else -> list.sortedByDescending { it.timestamp }
                }
                trySend(sorted)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting cast list")
                close(error.toException())
            }
        }
        castRef.addValueEventListener(listener)
        awaitClose { castRef.removeEventListener(listener) }
    }

    suspend fun getCast(castId: String): Cast? {
        return try {
            castRef.child(castId).get().await().getValue(Cast::class.java)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieCastIds(movieId: String): List<String> {
        return try {
            val snapshot = castMovieRef.child(movieId).get().await()
            snapshot.children.mapNotNull { it.key }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addCast(cast: Cast, castId: String) {
        castRef.child(castId).setValue(cast).await()
    }

    suspend fun updateCast(castId: String, updates: Map<String, Any?>) {
        castRef.child(castId).updateChildren(updates).await()
    }

    suspend fun updateMovieCast(movieId: String, castIds: Map<String, Any>): Void? =
        castMovieRef.child(movieId).setValue(castIds).await()
}
