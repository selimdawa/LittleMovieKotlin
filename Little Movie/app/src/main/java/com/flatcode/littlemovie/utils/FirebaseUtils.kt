package com.flatcode.littlemovie.utils

import android.widget.ImageView
import android.widget.TextView
import com.flatcode.littlemovie.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

fun ImageView.isInterested(id: String?, type: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
    ref.child(DATA.FirebaseUserUid).child(type!!).child(id!!)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    this@isInterested.setImageResource(R.drawable.ic_star_selected)
                    this@isInterested.tag = "added"
                } else {
                    this@isInterested.setImageResource(R.drawable.ic_star_unselected)
                    this@isInterested.tag = "add"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
}

fun ImageView.checkInterested(type: String?, id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).setValue(true)
        incrementInterestedCount(id, type, 1)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).removeValue()
        incrementInterestedCount(id, type, -1)
    }
}

private fun incrementInterestedCount(id: String?, type: String?, increment: Int) {
    val ref = FirebaseDatabase.getInstance().getReference(type!!).child(id!!)
        .child(DATA.INTERESTED_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var count = 0
            if (snapshot.exists()) {
                count = snapshot.value.toString().toInt()
            }
            ref.setValue(count + increment)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
    ref.child(userId!!).child(id!!).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                this@isFavorite.setImageResource(R.drawable.ic_heart_selected)
                this@isFavorite.tag = "added"
            } else {
                this@isFavorite.setImageResource(R.drawable.ic_heart_unselected)
                this@isFavorite.tag = "add"
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).setValue(true)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).removeValue()
    }
}

fun ImageView.isLoves(id: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
    ref.child(DATA.FirebaseUserUid).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                this@isLoves.setImageResource(R.drawable.ic_heart_selected)
                this@isLoves.tag = "added"
            } else {
                this@isLoves.setImageResource(R.drawable.ic_heart_unselected)
                this@isLoves.tag = "add"
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun TextView.nrLoves(id: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format("{0}", snapshot.childrenCount)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkLove(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).setValue(true)
        incrementLovesCount(id, 1)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).removeValue()
        incrementLovesCount(id, -1)
    }
}

private fun incrementLovesCount(id: String?, increment: Int) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(id!!)
        .child(DATA.LOVES_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var count = 0
            if (snapshot.exists()) {
                count = snapshot.value.toString().toInt()
            }
            ref.setValue(count + increment)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementViewCount() {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(this)
        .child(DATA.VIEWS_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = 0
            if (snapshot.exists()) {
                viewsCount = snapshot.value.toString().toInt()
            }
            ref.setValue(viewsCount + 1)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}
