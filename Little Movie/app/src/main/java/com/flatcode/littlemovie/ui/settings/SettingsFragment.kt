package com.flatcode.littlemovie.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.model.Setting
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.FragmentSettingsBinding
import com.flatcode.littlemovie.ui.cast.MyCastActivity
import com.flatcode.littlemovie.ui.category.MyCategoriesActivity
import com.flatcode.littlemovie.ui.profile.FavoritesActivity
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.flatcode.littlemovie.ui.profile.ProfileEditActivity
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private val list = ArrayList<Setting>()
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
        adapter = SettingAdapter(context, list)
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
                    binding.toolbar.imageProfile.GlideImage(true, it.profileImage)
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
        list.clear()
        list.add(Setting("1", "Edit Profile", R.drawable.ic_edit_white, 0, ProfileEditActivity::class.java))
        list.add(Setting("2", "My Cast", R.drawable.ic_cast, myCast, MyCastActivity::class.java))
        list.add(Setting("3", "My Categories", R.drawable.ic_category_gray, myCategories, MyCategoriesActivity::class.java))
        list.add(Setting("4", "Favorites", R.drawable.ic_star_selected, favorites, FavoritesActivity::class.java))
        list.add(Setting("5", "About App", R.drawable.ic_info, 0, null))
        list.add(Setting("6", "Logout", R.drawable.ic_logout_white, 0, null))
        list.add(Setting("7", "Share App", R.drawable.ic_share, 0, null))
        list.add(Setting("8", "Rate APP", R.drawable.ic_heart_selected, 0, null))
        list.add(Setting("9", "Privacy Policy", R.drawable.ic_privacy_policy, 0, PrivacyPolicyActivity::class.java))
        adapter.notifyDataSetChanged()
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
