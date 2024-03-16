package com.flatcode.littlemovieadmin.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Modelimport.Category
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityProfileBinding
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

        loadUserInfo()
        nrFavorites
        if (profileId == DATA.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { VOID.Intent1(context, CLASS.PROFILE_EDIT) }
            getNrItems(DATA.CAST, binding!!.numbercast)
            getNrItems(DATA.CATEGORIES, binding!!.numberCategories)
        } else {
            nrInterested(DATA.CAST, binding!!.numbercast)
            nrInterested(DATA.CATEGORIES, binding!!.numberCategories)
        }
        binding!!.back.setOnClickListener { onBackPressed() }
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
        val reference =
            FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).child(profileId!!)
                .child(database!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                text.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getNrItems(database: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Category::class.java)!!
                    if (item.publisher == profileId) i++
                }
                text.text = MessageFormat.format("{0}{1}", DATA.EMPTY, i)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val nrFavorites: Unit
        get() {
            val reference =
                FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(profileId!!)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding!!.numberFavorites.text =
                        MessageFormat.format("{0}", dataSnapshot.childrenCount)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    override fun onRestart() {
        loadUserInfo()
        super.onRestart()
    }

    override fun onResume() {
        loadUserInfo()
        super.onResume()
    }
}