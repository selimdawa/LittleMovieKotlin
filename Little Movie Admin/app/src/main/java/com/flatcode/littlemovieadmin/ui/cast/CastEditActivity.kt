package com.flatcode.littlemovieadmin.ui.cast

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityCastAddBinding
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.createProgressDialog
import com.flatcode.littlemovieadmin.utils.cropImageSquare
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.pickImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastEditActivity : BaseActivity() {

    private lateinit var binding: ActivityCastAddBinding
    private val viewModel: CastEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val castId = intent.getStringExtra(DATA.CAST_ID) ?: ""
        viewModel.init(castId)

        binding.toolbar.nameSpace.setText(R.string.edit_cast)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            requestStorage(DATA.MIX_SQUARE) {
                pickImage(DATA.MIX_SQUARE)
            }
        }
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
            progressDialog = createProgressDialog("Updating Cast...", "Please wait...")
            progressDialog?.show()
            viewModel.updateCast(name, aboutMy, imageUri) { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.cast?.let { cast ->
                        binding.nameEt.setText(cast.name)
                        binding.aboutMyEt.setText(cast.aboutMy)
                        if (imageUri == null) {
                            binding.image.loadImage(cast.image, isUser = true)
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
                cropImageSquare(uri)
            } else {
                val resultUri =
                    IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                if (resultUri != null) {
                    imageUri = resultUri
                    binding.image.setImageURI(imageUri)
                }
            }
        }
    }
}
