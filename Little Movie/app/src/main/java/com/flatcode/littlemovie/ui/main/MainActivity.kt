package com.flatcode.littlemovie.ui.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.VOID
import com.flatcode.littlemovie.databinding.ActivityMainBinding
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.nafis.bottomnavigation.NafisBottomNavigation
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var bottomNavigation: NafisBottomNavigation? = null
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
                left = systemBars.left,
                right = systemBars.right
            )
            binding!!.toolbar.root.updatePadding(top = systemBars.top)
            binding!!.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            insets
        }

        bottomNavigation = binding!!.bottomNavigation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigation!!.add(NafisBottomNavigation.Model(1, R.drawable.ic_settings))
        bottomNavigation!!.add(NafisBottomNavigation.Model(2, R.drawable.ic_home))
        bottomNavigation!!.add(NafisBottomNavigation.Model(3, R.drawable.ic_books))
        bottomNavigation!!.add(NafisBottomNavigation.Model(4, R.drawable.ic_group))
        bottomNavigation!!.setOnShowListener { item: NafisBottomNavigation.Model ->
            when (item.id) {
                1 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.settingsFragment)
                }

                2 -> {
                    binding!!.toolbar.card.visibility = View.VISIBLE
                    navController?.navigate(R.id.homeFragment)
                }

                3 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.myMoviesFragment)
                }

                4 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    navController?.navigate(R.id.categoriesFragment)
                }
            }
        }

        bottomNavigation!!.show(2, true)
        bottomNavigation!!.setOnClickMenuListener { item: NafisBottomNavigation.Model ->
            when (item.id) {
                1 -> Toast.makeText(applicationContext, R.string.settings, Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(applicationContext, R.string.home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, R.string.my_movies, Toast.LENGTH_SHORT).show()
                4 -> Toast.makeText(applicationContext, R.string.categories, Toast.LENGTH_SHORT).show()
            }
        }
        bottomNavigation!!.setOnReselectListener { item: NafisBottomNavigation.Model ->
            when (item.id) {
                1 -> Toast.makeText(applicationContext, R.string.settings, Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(applicationContext, R.string.home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, R.string.my_movies, Toast.LENGTH_SHORT).show()
                4 -> Toast.makeText(applicationContext, R.string.categories, Toast.LENGTH_SHORT).show()
            }
        }
        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(context, ProfileActivity::class.java, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        lifecycleScope.launch {
            viewModel.profileImageUrl.collect { profileImage ->
                Timber.d("Profile image URL updated: %s", profileImage)
                VOID.GlideImage(true, context, profileImage, binding!!.toolbar.image)
            }
        }
        viewModel.loadUserInfo()
    }

    override fun onBackPressed() {
        if (navController?.navigateUp() == false) {
            VOID.closeApp(context, activity)
        }
    }
}