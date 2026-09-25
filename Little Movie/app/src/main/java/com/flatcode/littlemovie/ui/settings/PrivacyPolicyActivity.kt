package com.flatcode.littlemovie.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityPrivacyPolicyBinding
import com.flatcode.littlemovie.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity() {

    private var binding: ActivityPrivacyPolicyBinding? = null
    private val viewModel: PrivacyPolicyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        observeViewModel()
        viewModel.loadPrivacyPolicy()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.privacyPolicy.collect { policy ->
                binding!!.text.text = policy
            }
        }
    }
}