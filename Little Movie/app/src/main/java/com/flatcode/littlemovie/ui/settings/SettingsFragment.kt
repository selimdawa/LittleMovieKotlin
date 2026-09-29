package com.flatcode.littlemovie.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.FragmentSettingsBinding
import com.flatcode.littlemovie.model.Setting
import com.flatcode.littlemovie.ui.cast.MyCastActivity
import com.flatcode.littlemovie.ui.category.MyCategoriesActivity
import com.flatcode.littlemovie.ui.profile.FavoritesActivity
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.flatcode.littlemovie.ui.profile.ProfileEditActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.dialogAboutApp
import com.flatcode.littlemovie.utils.dialogLogout
import com.flatcode.littlemovie.utils.loadImage
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.utils.rateApp
import com.flatcode.littlemovie.utils.shareApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var adapter: SettingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupAdapter()
        setupClickListeners()
        observeViewModel()

        return binding.root
    }

    private fun setupAdapter() {
        adapter = SettingAdapter { setting ->
            when (setting.id) {
                DATA.ABOUT_APP -> context?.dialogAboutApp()
                DATA.LOGOUT -> context?.dialogLogout()
                DATA.SHARE_APP -> context?.shareApp()
                DATA.RATE_APP -> context?.rateApp()
                else -> setting.c?.let {
                    val intent = Intent(context, it)
                    context?.startActivity(intent)
                }
            }
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.toolbar.item.setOnClickListener {
            context?.openActivity<ProfileActivity>(DATA.PROFILE_ID to DATA.FirebaseUserUid)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding.toolbar.imageProfile.loadImage(true, it.profileImage)
                    binding.toolbar.username.text = it.username
                    binding.toolbar.email.text = it.email
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.castCount,
                viewModel.categoriesCount,
                viewModel.favoritesCount
            ) { cast, categories, favorites ->
                Triple(cast, categories, favorites)
            }.collect { (cast, categories, favorites) ->
                loadSettings(cast, categories, favorites)
            }
        }
    }

    private fun loadSettings(myCast: Int, myCategories: Int, favorites: Int) {
        val settings = mutableListOf<Setting>()
        settings.add(
            Setting(
                DATA.EDIT_PROFILE,
                "Edit Profile",
                R.drawable.ic_edit_white,
                0,
                ProfileEditActivity::class.java
            )
        )
        settings.add(
            Setting(
                DATA.MY_CAST,
                "My Cast",
                R.drawable.ic_cast,
                myCast,
                MyCastActivity::class.java
            )
        )
        settings.add(
            Setting(
                DATA.MY_CATEGORIES,
                "My Categories",
                R.drawable.ic_category_gray,
                myCategories,
                MyCategoriesActivity::class.java
            )
        )
        settings.add(
            Setting(
                DATA.FAVORITES_ID,
                "Favorites",
                R.drawable.ic_star_selected,
                favorites,
                FavoritesActivity::class.java
            )
        )
        settings.add(Setting(DATA.ABOUT_APP, "About App", R.drawable.ic_info, 0, null))
        settings.add(Setting(DATA.LOGOUT, "Logout", R.drawable.ic_logout_white, 0, null))
        settings.add(Setting(DATA.SHARE_APP, "Share App", R.drawable.ic_share, 0, null))
        settings.add(Setting(DATA.RATE_APP, "Rate APP", R.drawable.ic_heart_selected, 0, null))
        settings.add(
            Setting(
                DATA.PRIVACY_POLICY_ID,
                "Privacy Policy",
                R.drawable.ic_privacy_policy,
                0,
                PrivacyPolicyActivity::class.java
            )
        )
        adapter.submitList(settings)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}