package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    database: FirebaseDatabase,
) {
    private val categoriesRef = database.getReference(DATA.CATEGORIES)

    suspend fun getCategories(orderBy: String): List<Category> {
        return try {
            val snapshot = categoriesRef.orderByChild(orderBy).get().await()
            snapshot.children.mapNotNull { it.getValue(Category::class.java) }.reversed()
        } catch (_: Exception) {
            emptyList()
        }
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
