package com.flatcode.littlemovie.ui.main

import android.app.Activity
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityMainBinding
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.flatcode.littlemovie.utils.*
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import io.selimdawa.bubblebottom.Model
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var bottomNavigation: BubbleBottomNavigation? = null
    private var navController: NavController? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left, right = systemBars.right
            )
            binding!!.toolbar.root.updatePadding(top = systemBars.top)
            binding!!.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            insets
        }

        bottomNavigation = binding!!.bottomNavigation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigation!!.add(Model(0, R.drawable.ic_settings))
        bottomNavigation!!.add(Model(1, R.drawable.ic_home))
        bottomNavigation!!.add(Model(2, R.drawable.ic_books))
        bottomNavigation!!.add(Model(3, R.drawable.ic_group))

        bottomNavigation!!.setOnClickMenuListener { model ->
            when (model.id) {
                0 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.settingsFragment)
                }

                1 -> {
                    binding!!.toolbar.card.visibility = View.VISIBLE
                    navController?.navigate(R.id.homeFragment)
                }

                2 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.myMoviesFragment)
                }

                3 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.categoriesFragment)
                }
            }
        }

        binding!!.toolbar.image.setOnClickListener {
            context.openActivity<ProfileActivity>(DATA.PROFILE_ID to DATA.FirebaseUserUid)
        }

        lifecycleScope.launch {
            viewModel.profileImageUrl.collect { profileImage ->
                Timber.d("Profile image URL updated: %s", profileImage)
                binding!!.toolbar.image.loadImage(true, profileImage)
            }
        }
        viewModel.loadUserInfo()
    }

    override fun onBackPressed() {
        if (navController?.navigateUp() == false) {
            context.closeApp(activity)
        }
    }
}