package com.flatcode.littlemovie.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.ProfileViewModel
import com.flatcode.littlemovie.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private val context: Context = this@ProfileActivity
    private val viewModel: ProfileViewModel by viewModels()
    private var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        if (profileId == DATA.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { VOID.Intent1(context, CLASS.PROFILE_EDIT) }
        }
        binding!!.back.setOnClickListener { onBackPressed() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding!!.username.text = it.username
                    VOID.GlideImage(true, context, it.profileImage, binding!!.profile)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.castCount.collect { count ->
                binding!!.numbercast.text = count.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.categoriesCount.collect { count ->
                binding!!.numberCategories.text = count.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.favoritesCount.collect { count ->
                binding!!.numberFavorites.text = count.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileId?.let { viewModel.loadData(it) }
    }
}
