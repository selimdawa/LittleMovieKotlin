package com.flatcode.littlemovieadmin.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.ProfileViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileId = intent.getStringExtra(DATA.PROFILE_ID) ?: ""
        viewModel.init(profileId)

        binding.back.setOnClickListener { onBackPressed() }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.user?.let { user ->
                        binding.username.text = user.username
                        VOID.GlideImage(true, this@ProfileActivity, user.profileImage, binding.profile)
                    }

                    binding.numberFavorites.text = state.favoritesCount.toString()
                    binding.numbercast.text = state.castCount.toString()
                    binding.numberCategories.text = state.categoriesCount.toString()

                    if (state.isMyProfile) {
                        binding.edit.visibility = View.VISIBLE
                        binding.edit.setImageResource(R.drawable.ic_edit_white)
                        binding.edit.setOnClickListener { VOID.Intent1(this@ProfileActivity, CLASS.PROFILE_EDIT) }
                    } else {
                        binding.edit.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}
