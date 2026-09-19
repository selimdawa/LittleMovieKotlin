package com.flatcode.littlemovieadmin.ui.cast

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
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.cropImageSquare
import com.flatcode.littlemovieadmin.utils.getFileExtension
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.ui.cast.CastEditViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityCastAddBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastEditActivity : BaseActivity() {

    private lateinit var binding: ActivityCastAddBinding
    private val viewModel: CastEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val castId = intent.getStringExtra(DATA.CAST_ID) ?: ""
        viewModel.init(castId)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.edit_cast)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.image.setOnClickListener { cropImageSquare() }
        binding.toolbar.ok.setOnClickListener { validateData() }

        observeState()
    }

    private fun validateData() {
        val name = binding.nameEt.text.toString().trim()
        val aboutMy = binding.aboutMyEt.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(aboutMy)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog?.setMessage("Updating Cast...")
            progressDialog?.show()
            val extension = imageUri?.let { getFileExtension(it) }
            viewModel.updateCast(name, aboutMy, imageUri, extension) { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) onBackPressed()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) progressDialog?.show() else progressDialog?.dismiss()
                    
                    state.cast?.let { cast ->
                        binding.nameEt.setText(cast.name)
                        binding.aboutMyEt.setText(cast.aboutMy)
                        if (imageUri == null) {
                            binding.image.loadGlideImage(cast.image, true)
                        }
                    }
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
                cropImageSquare()
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
