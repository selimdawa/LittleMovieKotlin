package com.flatcode.littlemovieadmin.ui.profile

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityProfileEditBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.ProgressDialog
import com.flatcode.littlemovieadmin.utils.cropImage
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.pickImage
import com.flatcode.littlemovieadmin.utils.requestStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.edit_profile)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            requestStorage(DATA.MIX_SQUARE) {
                pickImage(DATA.MIX_SQUARE)
            }
        }
        binding.go.setOnClickListener { validateData() }

        observeState()
    }

    private fun validateData() {
        val username = binding.nameEt.text.toString().trim()
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage("Updating profile...")
            dialog?.show()
            viewModel.updateProfile(username, imageUri) { success, message ->
                dialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.user?.let { user ->
                        binding.nameEt.setText(user.username)
                        if (imageUri == null) {
                            binding.profileImage.loadImage(true, user.profileImage)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == DATA.MIX_SQUARE) {
                pickImage(DATA.MIX_SQUARE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA.MIX_SQUARE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                cropImage(
                    uri = uri,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    isOval = true,
                    minWidth = DATA.MIX_SQUARE,
                    minHeight = DATA.MIX_SQUARE,
                    requestCode = DATA.MIX_SQUARE
                )
            } else {
                val resultUri =
                    IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                if (resultUri != null) {
                    imageUri = resultUri
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }
    }
}
