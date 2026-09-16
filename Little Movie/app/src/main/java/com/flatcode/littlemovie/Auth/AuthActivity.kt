package com.flatcode.littlemovie.Auth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.VOID
import dagger.hilt.android.AndroidEntryPoint
import com.flatcode.littlemovie.databinding.ActivityAuthBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var binding: ActivityAuthBinding? = null

    var context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        binding!!.loginBtn.setOnClickListener { VOID.Intent1(context, CLASS.LOGIN) }
        binding!!.skipBtn.setOnClickListener { VOID.Intent1(context, CLASS.REGISTER) }
    }
}