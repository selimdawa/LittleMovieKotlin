package com.flatcode.littlemovieadmin.Repository

import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CastRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val castRef = database.getReference(DATA.CAST)
    private val castMovieRef = database.getReference(DATA.CAST_MOVIE)

    suspend fun getCastList(orderBy: String): List<Cast> {
        val snapshot = castRef.orderByChild(orderBy).get().await()
        return snapshot.children.mapNotNull { it.getValue(Cast::class.java) }.reversed()
    }

    suspend fun getCast(castId: String): Cast? =
        castRef.child(castId).get().await().getValue(Cast::class.java)

    suspend fun getMovieCastIds(movieId: String): List<String> {
        val snapshot = castMovieRef.child(movieId).get().await()
        return snapshot.children.mapNotNull { it.key }
    }

    suspend fun addCast(cast: Cast, castId: String) =
        castRef.child(castId).setValue(cast).await()

    suspend fun updateCast(castId: String, updates: Map<String, Any?>) =
        castRef.child(castId).updateChildren(updates).await()

    suspend fun updateMovieCast(movieId: String, castIds: Map<String, Any>) =
        castMovieRef.child(movieId).setValue(castIds).await()

    suspend fun removeMovieCast(movieId: String, castId: String) =
        castMovieRef.child(movieId).child(castId).removeValue().await()

    suspend fun incrementCount(castId: String, field: String) =
        castRef.child(castId).child(field).setValue(ServerValue.increment(1)).await()

    suspend fun decrementCount(castId: String, field: String) =
        castRef.child(castId).child(field).setValue(ServerValue.increment(-1)).await()
}
