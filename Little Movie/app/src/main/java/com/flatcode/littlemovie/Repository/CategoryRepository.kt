package com.flatcode.littlemovie.repository

import com.flatcode.littlemovie.db.CategoryDao
import com.flatcode.littlemovie.db.InterestedDao
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.InterestedEntity
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val interestedDao: InterestedDao,
) {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference(DATA.CATEGORIES)
    private val interestedRef = database.getReference(DATA.INTERESTED)

    fun getCategories(publisherId: String? = null): Flow<List<Category>> {
        syncCategories(publisherId)
        return if (publisherId != null) {
            categoryDao.getCategoriesByPublisher(publisherId)
        } else {
            categoryDao.getAllCategories()
        }
    }

    fun getCategoryById(categoryId: String): Flow<Category?> {
        syncCategoryById(categoryId)
        return categoryDao.getCategoryById(categoryId)
    }

    fun getInterestedCategories(userId: String, orderBy: String): Flow<List<Category>> {
        syncCategories()
        syncInterested(userId)
        return interestedDao.getInterestedCategories(userId, DATA.CATEGORIES)
    }

    private fun syncCategories(publisherId: String? = null) {
        val query = if (publisherId != null) {
            categoriesRef.orderByChild(DATA.PUBLISHER).equalTo(publisherId)
        } else {
            categoriesRef
        }
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (data in snapshot.children) {
                    data.getValue(Category::class.java)?.let { list.add(it) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    categoryDao.insertCategories(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error syncing categories: %s", error.message)
            }
        })
    }

    private fun syncCategoryById(categoryId: String) {
        categoriesRef.child(categoryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Category::class.java)?.let { category ->
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryDao.insertCategories(listOf(category))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error syncing category %s: %s", categoryId, error.message)
            }
        })
    }

    private fun syncInterested(userId: String) {
        if (userId.isEmpty()) return
        interestedRef.child(userId).child(DATA.CATEGORIES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, DATA.CATEGORIES, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        interestedDao.deleteAllInterestedForUser(userId, DATA.CATEGORIES)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Error syncing interested categories: %s", error.message)
                }
            })
    }
}
