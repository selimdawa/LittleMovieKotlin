package com.flatcode.littlemovieadmin.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.databinding.ActivityLoginBinding
import com.flatcode.littlemovieadmin.ui.main.MainActivity
import com.flatcode.littlemovieadmin.utils.BaseActivity
import com.flatcode.littlemovieadmin.utils.ProgressDialog
import com.flatcode.littlemovieadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.forget.setOnClickListener { openActivity<ForgetPasswordActivity>() }
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
            dialog?.setMessage("Logging In...")
            dialog?.show()
            viewModel.login(email, password) { success, message ->
                dialog?.dismiss()
                if (success) {
                    openActivity<MainActivity>(clear = true)
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
                    if (state.isLoading) {
                        dialog?.setMessage("Logging In...")
                        dialog?.show()
                    } else {
                        dialog?.dismiss()
                    }
                }
            }
        }
    }
}
