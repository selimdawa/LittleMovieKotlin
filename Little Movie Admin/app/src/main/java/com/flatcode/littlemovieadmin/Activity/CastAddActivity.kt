package com.flatcode.littlemovieadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.ViewModel.CastAddViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityCastAddBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCastAddBinding
    private val viewModel: CastAddViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.add_new_cast)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.image.setOnClickListener { VOID.CropImageSquare(this) }
        binding.toolbar.ok.setOnClickListener { validateData() }

        observeState()
    }

    private fun validateData() {
        val name = binding.nameEt.text.toString().trim()
        val aboutMy = binding.aboutMyEt.text.toString().trim()
        val uri = imageUri

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(aboutMy)) {
            Toast.makeText(this, "Enter About My...", Toast.LENGTH_SHORT).show()
        } else if (uri == null) {
            Toast.makeText(this, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog?.setMessage("Uploading Cast...")
            progressDialog?.show()
            val extension = VOID.getFileExtension(uri, this)
            viewModel.uploadCast(name, aboutMy, uri, extension) { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) progressDialog?.show() else progressDialog?.dismiss()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSquare(this)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error! ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
