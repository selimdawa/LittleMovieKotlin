package com.flatcode.littlemovieadmin.Auth

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.LoginViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.forget.setOnClickListener { VOID.Intent1(this, CLASS.FORGET_PASSWORD) }
        binding.loginBtn.setOnClickListener { validateDate() }

        observeState()
    }

    private fun validateDate() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog?.setMessage("Logging In...")
            progressDialog?.show()
            viewModel.login(email, password) { success, message ->
                progressDialog?.dismiss()
                if (success) {
                    VOID.IntentClear(this, CLASS.MAIN)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) progressDialog?.show() else progressDialog?.dismiss()
                }
            }
        }
    }
}
