package com.flatcode.littlemovie.Activity

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.SplashViewModel
import com.flatcode.littlemovie.databinding.ActivitySplashBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

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
                    VOID.Intent1(context, CLASS.MAIN)
                } else {
                    VOID.Intent1(context, CLASS.AUTH)
                }
                finish()
            }
        }

        val timePerSecond = 2
        val timeFinal = time_per_millis * timePerSecond
        viewModel.checkUser(timeFinal.toLong())
    }

    companion object {
        const val time_per_millis = 1000
    }
}