package com.flatcode.littlemovieadmin.ui.movie

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityMovieEditBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.DATA.castMovie
import com.flatcode.littlemovieadmin.utils.convertDuration
import com.flatcode.littlemovieadmin.utils.createProgressDialog
import com.flatcode.littlemovieadmin.utils.cropVideoSquare
import com.flatcode.littlemovieadmin.utils.loadBlur
import com.flatcode.littlemovieadmin.utils.loadBlurUri
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.utils.pickImage
import com.flatcode.littlemovieadmin.utils.requestStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class MovieEditActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieEditBinding
    private val viewModel: MovieEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra(DATA.MOVIE_ID) ?: ""
        val categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        viewModel.init(movieId, categoryId)

        binding.toolbar.nameSpace.setText(R.string.edit_movie)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                castMovie.clear()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        })
        binding.category.setOnClickListener { categoryPickDialog() }
        binding.editImage.setOnClickListener {
            requestStorage(DATA.MIX_VIDEO_X) {
                pickImage(DATA.MIX_VIDEO_X)
            }
        }
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
            progressDialog = createProgressDialog("Updating Movie...", "Please wait...")
            progressDialog?.show()
            viewModel.updateMovie(
                name,
                description,
                yearText.toInt(),
                categoryId,
                imageUri,
                castMovie
            ) { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    castMovie.clear()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.movie?.let { movie ->
                        binding.nameEt.setText(movie.name)
                        binding.descriptionEt.setText(movie.description)
                        binding.yearEt.setText(movie.year.toString())
                        binding.duration.text = (movie.duration?.toLong() ?: 0L).convertDuration()
                        binding.cast.text = movie.castCount.toString()

                        if (imageUri == null) {
                            binding.image.loadImage(movie.image, isUser = true)
                            binding.imageBlur.loadBlur(movie.image, 50, isUser = false)
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == DATA.MIX_VIDEO_X) {
                pickImage(DATA.MIX_VIDEO_X)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA.MIX_VIDEO_X && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                cropVideoSquare(uri)
            } else {
                val resultUri =
                    IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                if (resultUri != null) {
                    imageUri = resultUri
                    binding.image.setImageURI(imageUri)
                    binding.imageBlur.loadBlurUri(imageUri, 50)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cast.text = MessageFormat.format("{0}{1}", DATA.EMPTY, castMovie.size)
        binding.cast.setOnClickListener { openActivity<CastMovieAddActivity>() }
    }

    override fun onDestroy() {
        super.onDestroy()
        castMovie.clear()
    }
}
