package com.flatcode.littlemovieadmin.Repository

import com.flatcode.littlemovieadmin.Modelimport.Category
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val categoriesRef = database.getReference(DATA.CATEGORIES)

    suspend fun getCategories(orderBy: String): List<Category> {
        val snapshot = categoriesRef.orderByChild(orderBy).get().await()
        return snapshot.children.mapNotNull { it.getValue(Category::class.java) }.reversed()
    }

    suspend fun getCategory(categoryId: String): Category? =
        categoriesRef.child(categoryId).get().await().getValue(Category::class.java)

    suspend fun addCategory(category: Category, categoryId: String) =
        categoriesRef.child(categoryId).setValue(category).await()

    suspend fun updateCategory(categoryId: String, updates: Map<String, Any?>) =
        categoriesRef.child(categoryId).updateChildren(updates).await()

    suspend fun incrementCount(categoryId: String, field: String) =
        categoriesRef.child(categoryId).child(field).setValue(ServerValue.increment(1)).await()

    suspend fun decrementCount(categoryId: String, field: String) =
        categoriesRef.child(categoryId).child(field).setValue(ServerValue.increment(-1)).await()
}
