package com.flatcode.littlemovie.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityProfileBinding
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.loadImage
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private val context: Context = this@ProfileActivity
    private val viewModel: ProfileViewModel by viewModels()
    private var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        if (profileId == DATA.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { context.openActivity<ProfileEditActivity>() }
        }
        binding!!.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding!!.username.text = it.username
                    binding!!.profile.loadImage(true, it.profileImage)
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
