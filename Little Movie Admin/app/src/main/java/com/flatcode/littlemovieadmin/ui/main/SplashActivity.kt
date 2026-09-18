package com.flatcode.littlemovieadmin.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.utils.CLASS
import com.flatcode.littlemovieadmin.utils.VOID
import com.flatcode.littlemovieadmin.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val timePerSecond = 2
    private val timeFinal = TIME_PER_MILLIS * timePerSecond

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({ checkUser() }, timeFinal.toLong())
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            VOID.Intent1(this, CLASS.LOGIN)
        } else {
            VOID.Intent1(this, CLASS.MAIN)
        }
        finish()
    }

    companion object {
        private const val TIME_PER_MILLIS = 1000
    }
}
