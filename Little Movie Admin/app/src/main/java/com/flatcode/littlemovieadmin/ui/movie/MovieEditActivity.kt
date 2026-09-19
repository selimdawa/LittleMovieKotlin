package com.flatcode.littlemovieadmin.ui.movie

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.DATA.castMovie
import com.flatcode.littlemovieadmin.utils.convertDuration
import com.flatcode.littlemovieadmin.utils.cropVideoSquare
import com.flatcode.littlemovieadmin.utils.loadGlideBlur
import com.flatcode.littlemovieadmin.utils.loadGlideBlurUri
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieEditViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityMovieEditBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MovieEditActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieEditBinding
    private val viewModel: MovieEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra(DATA.MOVIE_ID) ?: ""
        val categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        viewModel.init(movieId, categoryId)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.edit_movie)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.category.setOnClickListener { categoryPickDialog() }
        binding.editImage.setOnClickListener { cropVideoSquare() }
        binding.toolbar.ok.setOnClickListener { validateData() }

        observeState()
    }

    private fun validateData() {
        val name = binding.nameEt.text.toString().trim()
        val description = binding.descriptionEt.text.toString().trim()
        val yearText = binding.yearEt.text.toString()
        val categoryId = viewModel.uiState.value.selectedCategoryId

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(yearText)) {
            Toast.makeText(this, "Enter Date...", Toast.LENGTH_SHORT).show()
        } else if (yearText.toInt() < DATA.MIN_YEAR || yearText.toInt() > DATA.MAX_YEAR) {
            Toast.makeText(this, "Invalid Date...", Toast.LENGTH_SHORT).show()
        } else if (categoryId.isNullOrEmpty()) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show()
        } else if (castMovie.isEmpty()) {
            Toast.makeText(this, "Enter Cast...", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog?.setMessage("Updating Movie...")
            progressDialog?.show()
            viewModel.updateMovie(name, description, yearText.toInt(), categoryId, imageUri, castMovie) { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    castMovie.clear()
                    onBackPressed()
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) progressDialog?.show() else progressDialog?.dismiss()
                    
                    state.movie?.let { movie ->
                        binding.nameEt.setText(movie.name)
                        binding.descriptionEt.setText(movie.description)
                        binding.yearEt.setText(movie.year.toString())
                        binding.duration.text = (movie.duration?.toLong() ?: 0L).convertDuration()
                        binding.cast.text = movie.castCount.toString()
                        
                        if (imageUri == null) {
                            binding.image.loadGlideImage(movie.image, true)
                            binding.imageBlur.loadGlideBlur(movie.image, 50, false)
                        }
                    }
                    
                    binding.category.text = state.selectedCategoryName
                }
            }
        }
    }

    private fun categoryPickDialog() {
        val categories = viewModel.uiState.value.categories
        if (categories.isEmpty()) return

        val categoryNames = categories.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Pick Category")
            .setItems(categoryNames) { _, which ->
                viewModel.setCategoryId(categories[which].id, categories[which].name)
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                cropVideoSquare()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding.image.setImageURI(imageUri)
                binding.imageBlur.loadGlideBlurUri(imageUri, 50)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error! ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cast.text = MessageFormat.format("{0}{1}", DATA.EMPTY, castMovie.size)
        binding.cast.setOnClickListener { openActivity<CastMovieAddActivity>() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        castMovie.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        castMovie.clear()
    }
}
