package com.flatcode.littlemovie.ui.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityMainBinding
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.closeApp
import com.flatcode.littlemovie.utils.loadImage
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.Model
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var activity: Activity? = null
    private val context: Context = also { activity = it }

    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.navigateUp().not()) {
                    context.closeApp()
                }
            }
        })

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            _binding?.toolbar?.card?.visibility = if (destination.id == R.id.homeFragment) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        binding.bottomNavigation.apply {
            add(Model(R.id.settingsFragment, R.drawable.ic_settings))
            add(Model(R.id.homeFragment, R.drawable.ic_home))
            add(Model(R.id.myMoviesFragment, R.drawable.ic_books))
            add(Model(R.id.categoriesFragment, R.drawable.ic_group))

            setOnShowListener { item -> navController.navigate(item.id) }
            show(R.id.homeFragment, true)
        }

        binding.toolbar.image.setOnClickListener {
            context.openActivity<ProfileActivity>(DATA.PROFILE_ID to DATA.FirebaseUserUid)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileImageUrl.collectLatest { profileImage ->
                    binding.toolbar.image.loadImage(true, profileImage)
                }
            }
        }
        viewModel.loadUserInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity = null
        _binding = null
    }
}