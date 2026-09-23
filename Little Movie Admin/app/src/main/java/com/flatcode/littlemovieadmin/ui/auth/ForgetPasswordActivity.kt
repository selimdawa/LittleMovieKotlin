package com.flatcode.littlemovieadmin.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.databinding.ActivityForgetPasswordBinding
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.utils.createProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val context: Context = this@ForgetPasswordActivity
    private val viewModel: ForgetPasswordViewModel by viewModels()
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.go.setOnClickListener { validateDate() }
        binding.login.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        observeState()
    }

    private var email = ""
    private fun validateDate() {
        email = binding.emailEt.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(context, "Enter email...!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email format...!", Toast.LENGTH_SHORT).show()
        } else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        dialog = createProgressDialog(
            "Sending password recovery instructions to $email",
            "Please wait..."
        )
        dialog?.show()
        viewModel.recoverPassword(email) { _, message ->
            dialog?.dismiss()
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) {
                        if (dialog == null) {
                            dialog = createProgressDialog(
                                "Sending password recovery instructions to $email",
                                "Please wait..."
                            )
                        }
                        dialog?.show()
                    } else {
                        dialog?.dismiss()
                    }
                }
            }
        }
    }
}
