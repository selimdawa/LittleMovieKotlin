package com.flatcode.littlemovie.ui.auth

import com.flatcode.littlemovie.utils.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.flatcode.littlemovie.databinding.ActivityForgetPasswordBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.openActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordActivity : BaseActivity() {

    private var binding: ActivityForgetPasswordBinding? = null
    private val context: Context = this@ForgetPasswordActivity
    private var auth: FirebaseAuth? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.noAccount.setOnClickListener {
            context.openActivity<RegisterActivity>()
            finish()
        }
        binding!!.login.setOnClickListener {
            context.openActivity<LoginActivity>()
            finish()
        }
        binding!!.go.setOnClickListener { validateDate() }
    }

    private var email = ""
    private fun validateDate() {
        email = binding!!.emailEt.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            Toast.makeText(context, "Enter email...!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email format...!", Toast.LENGTH_SHORT).show()
        } else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        dialog!!.setMessage("Sending password recovery to instructions to $email")
        dialog!!.show()
        auth!!.sendPasswordResetEmail(email).addOnCompleteListener {
            dialog!!.dismiss()
            Toast.makeText(
                context, "Instructions to reset password sent to $email", Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(context, "Failed to send to due to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }
}