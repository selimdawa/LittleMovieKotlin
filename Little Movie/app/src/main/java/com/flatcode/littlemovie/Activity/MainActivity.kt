package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Fragment.CategoriesFragment
import com.flatcode.littlemovie.Fragment.HomeFragment
import com.flatcode.littlemovie.Fragment.SettingsFragment
import com.flatcode.littlemovie.Fragment.myMoviesFragment
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.MainViewModel
import com.flatcode.littlemovie.databinding.ActivityMainBinding
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
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        bottomNavigation = binding!!.bottomNavigation
        bottomNavigation!!.add(NafisBottomNavigation.Model(1, R.drawable.ic_settings))
        bottomNavigation!!.add(NafisBottomNavigation.Model(2, R.drawable.ic_home))
        bottomNavigation!!.add(NafisBottomNavigation.Model(3, R.drawable.ic_books))
        bottomNavigation!!.add(NafisBottomNavigation.Model(4, R.drawable.ic_group))
        bottomNavigation!!.setOnShowListener { item: NafisBottomNavigation.Model ->
            var fragment: Fragment? = null
            when (item.id) {
                1 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = SettingsFragment()
                }

                2 -> {
                    binding!!.toolbar.card.visibility = View.VISIBLE
                    fragment = HomeFragment()
                }

                3 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = myMoviesFragment()
                }

                4 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = CategoriesFragment()
                }
            }
            loadFragment(fragment)
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
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        lifecycleScope.launch {
            viewModel.profileImageUrl.collect { profileImage ->
                Timber.d("Profile image URL updated: %s", profileImage)
                VOID.GlideImage(true, context, profileImage, binding!!.toolbar.image)
            }
        }
        viewModel.loadUserInfo()
    }

    private fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainer, fragment!!
        ).commit()
    }

    override fun onBackPressed() {
        VOID.closeApp(context, activity)
    }
}