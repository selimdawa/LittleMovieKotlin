package com.flatcode.littlemovie.ui.auth

import com.flatcode.littlemovie.utils.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ActivityLoginBinding
import com.flatcode.littlemovie.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private var binding: ActivityLoginBinding? = null
    var context: Context = this@LoginActivity
    private var auth: FirebaseAuth? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.forget.setOnClickListener { context.openActivity<ForgetPasswordActivity>() }
        binding!!.noAccount.setOnClickListener { context.openActivity<RegisterActivity>() }
        binding!!.loginBtn.setOnClickListener { validateDate() }
    }

    private var email = ""
    private var password = ""
    private fun validateDate() {

        //get data
        email = binding!!.emailEt.text.toString().trim { it <= ' ' }
        password = binding!!.passwordEt.text.toString().trim { it <= ' ' }

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        dialog!!.setMessage("Logging In...")
        dialog!!.show()
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { context.openActivity<MainActivity>(clear = true) }
            .addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(context, DATA.EMPTY + e.message, Toast.LENGTH_SHORT).show()
            }
    }
}