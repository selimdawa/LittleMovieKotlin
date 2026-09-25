package com.flatcode.littlemovie.ui.auth

import android.content.Context
import android.os.Bundle
import com.flatcode.littlemovie.databinding.ActivityAuthBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private var binding: ActivityAuthBinding? = null

    var context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.loginBtn.setOnClickListener { context.openActivity<LoginActivity>() }
        binding!!.skipBtn.setOnClickListener { context.openActivity<RegisterActivity>() }
    }
}