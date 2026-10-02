package com.flatcode.littlemovie.ui.profile

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityProfileEditBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.ProgressDialog
import com.flatcode.littlemovie.utils.getFileExtension
import com.flatcode.littlemovie.utils.isNetworkAvailable
import com.flatcode.littlemovie.utils.loadImage
import com.flatcode.littlemovie.utils.startCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity() {

    private var binding: ActivityProfileEditBinding? = null
    private val context: Context = this@ProfileEditActivity
    private val viewModel: ProfileEditViewModel by viewModels()

    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.let { intent ->
                    IntentCompat.getParcelableExtra(intent, "CROP_RESULT_URI", Uri::class.java)
                }
                binding!!.profileImage.setImageURI(null)
                binding!!.profileImage.setImageURI(imageUri)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                cropImageLauncher.launch(context.startCropActivity(it, 1, 1, true))
            }
        }

    private fun launchImagePicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding!!.profileImage.loadImage(true, it.profileImage)
                    binding!!.nameEt.setText(it.username)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.imageUploadStatus.collect { result ->
                result?.let {
                    if (it.isFailure) {
                        dialog!!.dismiss()
                        Toast.makeText(
                            context,
                            "Failed to upload image: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateStatus.collect { result ->
                result?.let {
                    dialog!!.dismiss()
                    if (it.isSuccess) {
                        Toast.makeText(context, "Profile updated...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to update profile: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetStatus()
                }
            }
        }
    }

    private fun validateData() {
        val username = binding!!.nameEt.text.toString().trim()
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Enter name...", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        } else {
            dialog!!.setMessage("Updating profile...")
            dialog!!.show()
            val extension = imageUri?.getFileExtension(context)
            viewModel.updateProfile(username, imageUri, extension)
        }
    }
}