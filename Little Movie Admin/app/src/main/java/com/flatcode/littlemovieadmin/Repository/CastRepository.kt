package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CastRepository @Inject constructor(
    database: FirebaseDatabase,
) {
    private val castRef = database.getReference(DATA.CAST)
    private val castMovieRef = database.getReference(DATA.CAST_MOVIE)

    suspend fun getCastList(orderBy: String): List<Cast> {
        return try {
            val snapshot = castRef.orderByChild(orderBy).get().await()
            snapshot.children.mapNotNull { it.getValue(Cast::class.java) }.reversed()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getCast(castId: String): Cast? {
        return try {
            castRef.child(castId).get().await().getValue(Cast::class.java)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieCastIds(movieId: String): List<String> {
        val snapshot = castMovieRef.child(movieId).get().await()
        return snapshot.children.mapNotNull { it.key }
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
