package com.flatcode.littlemovieadmin.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

fun incrementItemRemoveCount(database: String?, id: String?, childDB: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(database!!)
    ref.child(id!!).child(childDB!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentCount = snapshot.getValue(Long::class.java) ?: 0L
            if (currentCount > 0) {
                ref.child(id).child(childDB).setValue(currentCount - 1)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "incrementItemRemoveCount cancelled")
        }
    })
}

fun deleteMovieInfo(id: String) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE).child(id)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                incrementItemRemoveCount(DATA.CAST, snapshot.key, DATA.MOVIES_COUNT)
            }
            ref.removeValue()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun deleteCastInfo(id: String) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                if (snapshot.hasChild(id)) {
                    ref.child(snapshot.key!!).child(id).removeValue()
                    incrementItemRemoveCount(DATA.MOVIES, snapshot.key, DATA.CAST_COUNT)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun Context.addToEditorsChoice(activity: Activity, id: String?, number: Int) {
    val dialog = createProgressDialog("Updating Editors Choice...")
    dialog.show()
    val hashMap = HashMap<String, Any>()
    hashMap[DATA.EDITORS_CHOICE] = number
    val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
    reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
        dialog.dismiss()
        Toast.makeText(this, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
        activity.finish()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT).show()
    }
}
