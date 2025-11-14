package com.flatcode.littlemovieadmin.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityPrivacyPolicyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrivacyPolicyActivity : AppCompatActivity() {

    private var binding: ActivityPrivacyPolicyBinding? = null
    var activity: Activity = this@PrivacyPolicyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.edit.setOnClickListener { VOID.Intent1(activity, CLASS.PRIVACY_POLICY_EDIT) }

        VOID.Logo(activity, binding!!.logo)
        privacyPolicy()
    }

    private fun privacyPolicy() {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.TOOLS)
            .child(DATA.PRIVACY_POLICY)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.value.toString()
                binding!!.text.text = name
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onRestart() {
        privacyPolicy()
        super.onRestart()
    }

    override fun onResume() {
        privacyPolicy()
        super.onResume()
    }
}