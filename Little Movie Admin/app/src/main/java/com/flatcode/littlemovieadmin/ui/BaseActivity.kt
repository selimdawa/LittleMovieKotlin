package com.flatcode.littlemovieadmin.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flatcode.littlemovieadmin.utils.checkStoragePermission
import com.flatcode.littlemovieadmin.utils.checkVideoPermission
import com.flatcode.littlemovieadmin.utils.requestStoragePermission
import com.flatcode.littlemovieadmin.utils.requestVideoPermission

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        val view = layoutInflater.inflate(layoutResID, null)
        setContentView(view)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    fun requestStorage(requestCode: Int, onGranted: () -> Unit) {
        if (checkStoragePermission()) {
            onGranted()
        } else {
            requestStoragePermission(requestCode)
        }
    }

    fun requestVideo(requestCode: Int, onGranted: () -> Unit) {
        if (checkVideoPermission()) {
            onGranted()
        } else {
            requestVideoPermission(requestCode)
        }
    }
}
