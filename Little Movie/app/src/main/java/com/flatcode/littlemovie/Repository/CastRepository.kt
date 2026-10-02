package com.flatcode.littlemovie.repository

import com.flatcode.littlemovie.db.CastDao
import com.flatcode.littlemovie.db.InterestedDao
import com.flatcode.littlemovie.model.Cast
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
class CastRepository @Inject constructor(
    private val castDao: CastDao,
    private val interestedDao: InterestedDao,
) {

    private val database = FirebaseDatabase.getInstance()
    private val castRef = database.getReference(DATA.CAST)
    private val interestedRef = database.getReference(DATA.INTERESTED)

    fun getCast(orderBy: String = DATA.TIMESTAMP): Flow<List<Cast>> {
        syncCast(orderBy)
        return castDao.getAllCasts()
    }

    fun getCastByIds(castIds: List<String>): Flow<List<Cast>> {
        syncCast()
        return castDao.getCastsByIds(castIds)
    }

    fun getInterestedCast(userId: String, orderBy: String): Flow<List<Cast>> {
        syncCast(orderBy)
        syncInterested(userId)
        return interestedDao.getInterestedCasts(userId, DATA.CAST)
    }

    private fun syncCast(orderBy: String = DATA.TIMESTAMP) {
        val query = if (orderBy.isNotEmpty()) castRef.orderByChild(orderBy) else castRef
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Cast>()
                for (data in snapshot.children) {
                    data.getValue(Cast::class.java)?.let { list.add(it) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    castDao.deleteAllCasts()
                    castDao.insertCasts(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error syncing cast: %s", error.message)
            }
        })
    }

    private fun syncInterested(userId: String) {
        if (userId.isEmpty()) return
        interestedRef.child(userId).child(DATA.CAST)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, DATA.CAST, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        interestedDao.deleteAllInterestedForUser(userId, DATA.CAST)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Error syncing interested cast: %s", error.message)
                }
            })
    }
}
