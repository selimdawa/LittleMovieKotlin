package com.flatcode.littlemovieadmin.Activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.PrivacyPolicyViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityPrivacyPolicyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    private val viewModel: PrivacyPolicyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.edit.setOnClickListener { VOID.Intent1(this, CLASS.PRIVACY_POLICY_EDIT) }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.text.text = state.content
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPrivacyPolicy()
    }
}
