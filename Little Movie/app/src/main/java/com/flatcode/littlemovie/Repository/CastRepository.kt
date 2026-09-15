package com.flatcode.littlemovie.Repository

import com.flatcode.littlemovie.Data.Local.Dao.CastDao
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Unit.DATA
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

class CastRepository @Inject constructor(
    private val castDao: CastDao
) {

    private val database = FirebaseDatabase.getInstance()
    private val castRef = database.getReference(DATA.CAST)
    private val interestedRef = database.getReference(DATA.INTERESTED)

    fun getCast(orderBy: String): Flow<List<Cast>> = callbackFlow {
        Timber.d("Fetching cast ordered by %s", orderBy)
        val query = castRef.orderByChild(orderBy)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Cast>()
                for (data in snapshot.children) {
                    data.getValue(Cast::class.java)?.let { list.add(it) }
                }
                list.reverse()
                
                // Save to local DB
                launch {
                    castDao.insertCasts(list)
                }
                
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching cast: %s", error.message)
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getCastByIds(castIds: List<String>): Flow<List<Cast>> = callbackFlow {
        val listener = castRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Cast>()
                for (data in snapshot.children) {
                    val cast = data.getValue(Cast::class.java)
                    cast?.let {
                        if (castIds.contains(it.id)) {
                            list.add(it)
                        }
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { castRef.removeEventListener(listener) }
    }

    fun getInterestedCast(userId: String, orderBy: String): Flow<List<Cast>> = callbackFlow {
        val interestedListener = interestedRef.child(userId).child(DATA.CAST).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val castIds = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { castIds.add(it) }
                }
                
                castRef.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(castSnapshot: DataSnapshot) {
                        val list = mutableListOf<Cast>()
                        for (data in castSnapshot.children) {
                            val cast = data.getValue(Cast::class.java)
                            if (cast != null && castIds.contains(cast.id)) {
                                list.add(cast)
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
        awaitClose { interestedRef.child(userId).child(DATA.CAST).removeEventListener(interestedListener) }
    }
}
