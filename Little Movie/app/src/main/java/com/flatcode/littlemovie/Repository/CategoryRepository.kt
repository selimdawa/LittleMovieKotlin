package com.flatcode.littlemovie.repository

import com.flatcode.littlemovie.db.CategoryDao
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference(DATA.CATEGORIES)
    private val interestedRef = database.getReference(DATA.INTERESTED)

    fun getCategories(publisherId: String? = null): Flow<List<Category>> = callbackFlow {
        Timber.d("Fetching categories%s", if (publisherId != null) " for publisher: $publisherId" else "")
        val query = if (publisherId != null) {
            categoriesRef.orderByChild(DATA.PUBLISHER).equalTo(publisherId)
        } else {
            categoriesRef
        }
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (data in snapshot.children) {
                    data.getValue(Category::class.java)?.let { list.add(it) }
                }
                
                // Save to local DB
                launch {
                    categoryDao.insertCategories(list)
                }
                
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching categories: %s", error.message)
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getCategoryById(categoryId: String): Flow<Category?> = callbackFlow {
        val listener = categoriesRef.child(categoryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Category::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { categoriesRef.child(categoryId).removeEventListener(listener) }
    }

    fun getCategoriesCount(): Flow<Long> = callbackFlow {
        val listener = categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { categoriesRef.removeEventListener(listener) }
    }

    fun getInterestedCategories(userId: String, orderBy: String): Flow<List<Category>> = callbackFlow {
        val interestedListener = interestedRef.child(userId).child(DATA.CATEGORIES).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryIds = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { categoryIds.add(it) }
                }
                
                categoriesRef.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(categorySnapshot: DataSnapshot) {
                        val list = mutableListOf<Category>()
                        for (data in categorySnapshot.children) {
                            val category = data.getValue(Category::class.java)
                            if (category != null && categoryIds.contains(category.id)) {
                                list.add(category)
                            }
                        }
                        list.reverse()
                        trySend(list)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { interestedRef.child(userId).child(DATA.CATEGORIES).removeEventListener(interestedListener) }
    }
}
