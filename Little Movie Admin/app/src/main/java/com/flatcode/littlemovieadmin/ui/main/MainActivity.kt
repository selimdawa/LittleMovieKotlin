package com.flatcode.littlemovieadmin.ui.main

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.os.Bundle
import com.flatcode.littlemovieadmin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}