package com.flatcode.littlemovie.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    var context: Context = this@ProfileActivity
    var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val intent = intent
        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        if (profileId == DATA.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { VOID.Intent1(context, CLASS.PROFILE_EDIT) }
        }
        binding!!.back.setOnClickListener { onBackPressed() }
    }

    private fun start() {
        loadUserInfo()
        nrFavorites
        nrInterested(DATA.CAST, binding!!.numbercast)
        nrInterested(DATA.CATEGORIES, binding!!.numberCategories)
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(profileId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //String email = DATA.EMPTY + snapshot.child(DATA.EMAIL).getValue();
                val username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).value
                val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value
                //String timestamp = DATA.EMPTY + snapshot.child(DATA.TIMESTAMP).getValue();
                //String id = DATA.EMPTY + snapshot.child(DATA.ID).getValue();
                //int version = DATA.ZERO + snapshot.child(DATA.VERSION).getValue();
                binding!!.username.text = username
                VOID.GlideImage(true, context, profileImage, binding!!.profile)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nrInterested(database: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(profileId!!).child(database!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                text.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val nrFavorites: Unit
        get() {
            val ref = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(profileId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding!!.numberFavorites.text =
                        MessageFormat.format("{0}", dataSnapshot.childrenCount)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    override fun onRestart() {
        start()
        super.onRestart()
    }

    override fun onResume() {
        start()
        super.onResume()
    }
}