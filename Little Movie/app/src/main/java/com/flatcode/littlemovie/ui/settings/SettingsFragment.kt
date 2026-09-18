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
import com.flatcode.littlemovie.utils.CLASS
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.VOID
import com.flatcode.littlemovie.databinding.FragmentSettingsBinding
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
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    VOID.GlideImage(true, context, it.profileImage, binding.toolbar.imageProfile)
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
        list.add(Setting("1", "Edit Profile", R.drawable.ic_edit_white, 0, CLASS.PROFILE_EDIT))
        list.add(Setting("2", "My Cast", R.drawable.ic_cast, myCast, CLASS.MY_CAST))
        list.add(Setting("3", "My Categories", R.drawable.ic_category_gray, myCategories, CLASS.MY_CATEGORIES))
        list.add(Setting("4", "Favorites", R.drawable.ic_star_selected, favorites, CLASS.FAVORITES))
        list.add(Setting("5", "About App", R.drawable.ic_info, 0, null))
        list.add(Setting("6", "Logout", R.drawable.ic_logout_white, 0, null))
        list.add(Setting("7", "Share App", R.drawable.ic_share, 0, null))
        list.add(Setting("8", "Rate APP", R.drawable.ic_heart_selected, 0, null))
        list.add(Setting("9", "Privacy Policy", R.drawable.ic_privacy_policy, 0, CLASS.PRIVACY_POLICY))
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
