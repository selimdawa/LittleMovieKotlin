package com.flatcode.littlemovie.Auth

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    var context: Context = this@RegisterActivity
    private var auth: FirebaseAuth? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        VOID.Logo(baseContext, binding!!.logo)
        VOID.Intro(baseContext, binding!!.background, binding!!.backWhite, binding!!.backBlack)

        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.forget.setOnClickListener { VOID.Intent1(context, CLASS.FORGET_PASSWORD) }
        binding!!.login.setOnClickListener {
            VOID.Intent1(context, CLASS.LOGIN)
            finish()
        }
        binding!!.go.setOnClickListener { validateData() }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        email = binding!!.emailEt.text.toString().trim { it <= ' ' }
        password = binding!!.passwordEt.text.toString().trim { it <= ' ' }
        val cPassword = binding!!.cPasswordEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter you name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(context, "Confirm Password...!", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(context, "Password doesn't match...!", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //show progress
        dialog!!.setMessage("Creating account...")
        dialog!!.show()

        //create user in firebase auth
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { updateUserinfo() }
            .addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(context, DATA.EMPTY + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserinfo() {
        dialog!!.setMessage("Saving user info...")
        //get current user uid, since user is registered so we can get now
        val id = auth!!.uid

        //setup data to add in db
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.EMAIL] = DATA.EMPTY + email
        hashMap[DATA.ID] = DATA.EMPTY + id
        hashMap[DATA.PROFILE_IMAGE] = DATA.EMPTY + DATA.BASIC
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.USER_NAME] = DATA.EMPTY + name
        hashMap[DATA.VERSION] = DATA.CURRENT_VERSION

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            //data added to db
            dialog!!.dismiss()
            Toast.makeText(context, "Account created...", Toast.LENGTH_SHORT).show()
            VOID.IntentClear(context, CLASS.MAIN)
            finish()
        }.addOnFailureListener { e: Exception ->
            //data failed adding to db
            Toast.makeText(context, DATA.EMPTY + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}