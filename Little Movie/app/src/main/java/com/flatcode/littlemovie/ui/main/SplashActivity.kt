package com.flatcode.littlemovie.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.databinding.ActivitySplashBinding
import com.flatcode.littlemovie.ui.auth.AuthActivity
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private var binding: ActivitySplashBinding? = null
    private val context: Context = this@SplashActivity
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        lifecycleScope.launch {
            viewModel.isLoggedIn.filterNotNull().collect { isLoggedIn ->
                Timber.d("User login status collected: %b", isLoggedIn)
                if (isLoggedIn) {
                    context.openActivity<MainActivity>()
                } else {
                    context.openActivity<AuthActivity>()
                }
                finish()
            }
        }

        val timeFinal = 2000
        viewModel.checkUser(timeFinal.toLong())
    }
}