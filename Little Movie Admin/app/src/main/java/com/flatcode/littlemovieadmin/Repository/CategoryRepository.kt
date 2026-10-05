package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.Category
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
class CategoryRepository @Inject constructor(
    database: FirebaseDatabase,
) {
    private val categoriesRef = database.getReference(DATA.CATEGORIES)

    fun getCategories(orderBy: String): Flow<List<Category>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                val sorted = when (orderBy) {
                    DATA.NAME -> list.sortedBy { it.name }
                    DATA.MOVIES_COUNT -> list.sortedByDescending { it.moviesCount }
                    DATA.INTERESTED_COUNT -> list.sortedByDescending { it.interestedCount }
                    else -> list.sortedByDescending { it.timestamp }
                }
                trySend(sorted)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting categories")
                trySend(emptyList())
            }
        }
        categoriesRef.addValueEventListener(listener)
        awaitClose { categoriesRef.removeEventListener(listener) }
    }

    suspend fun getCategory(categoryId: String): Category? {
        return try {
            categoriesRef.child(categoryId).get().await().getValue(Category::class.java)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun addCategory(category: Category, categoryId: String) {
        categoriesRef.child(categoryId).setValue(category).await()
    }

    suspend fun updateCategory(categoryId: String, updates: Map<String, Any?>) {
        categoriesRef.child(categoryId).updateChildren(updates).await()
    }
}
