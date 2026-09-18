package com.flatcode.littlemovieadmin.ui.privacypolicy

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityPrivacyPolicyEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyEditActivity : BaseActivity() {

    private lateinit var binding: ActivityPrivacyPolicyEditBinding
    private val viewModel: PrivacyPolicyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.go.setOnClickListener { validateData() }

        observeState()
    }

    private fun validateData() {
        val content = binding.text.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Enter Privacy Policy...", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.updatePrivacyPolicy(content) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) onBackPressed()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (binding.text.text.isEmpty()) {
                        binding.text.setText(state.content)
                    }
                }
            }
        }
    }
}
