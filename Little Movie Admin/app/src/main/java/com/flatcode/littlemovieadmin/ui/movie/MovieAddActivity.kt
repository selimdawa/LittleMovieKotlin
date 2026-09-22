package com.flatcode.littlemovieadmin.ui.movie

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.DATA.castMovie
import com.flatcode.littlemovieadmin.utils.convertDuration
import com.flatcode.littlemovieadmin.utils.cropVideoSquare
import com.flatcode.littlemovieadmin.utils.createProgressDialog
import com.flatcode.littlemovieadmin.utils.loadBlurUri
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.databinding.ActivityMovieAddBinding
import com.flatcode.littlemovieadmin.utils.pickImage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MovieAddActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieAddBinding
    private val viewModel: MovieAddViewModel by viewModels()
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var progressDialog: AlertDialog? = null
    private var durations: String? = null
    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.add_new_movie)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.category.setOnClickListener { categoryPickDialog() }
        binding.image.setOnClickListener {
            requestStorage(DATA.MIX_VIDEO_X) {
                pickImage(DATA.MIX_VIDEO_X)
            }
        }
        binding.chooseMovie.setOnClickListener {
            requestVideo(101) {
                openVideoFiles()
            }
        }
        binding.toolbar.ok.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val name = binding.nameEt.text.toString().trim()
        val description = binding.descriptionEt.text.toString().trim()
        val yearText = binding.yearEt.text.toString()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(yearText)) {
            Toast.makeText(this, "Enter Date...", Toast.LENGTH_SHORT).show()
        } else if (yearText.toInt() < DATA.MIN_YEAR || yearText.toInt() > DATA.MAX_YEAR) {
            Toast.makeText(this, "Invalid Date...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show()
        } else if (castMovie.isEmpty()) {
            Toast.makeText(this, "Enter Cast...", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(this, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else if (videoUri == null) {
            Toast.makeText(this, "Pick Movie...", Toast.LENGTH_SHORT).show()
        } else {
            uploadMovie(name, description, yearText.toInt())
        }
    }

    private fun uploadMovie(name: String, description: String, year: Int) {
        val iUri = imageUri ?: return
        val vUri = videoUri ?: return
        val cId = selectedCategoryId ?: return

        progressDialog = createProgressDialog("Uploading Movie...", "Please wait...")
        progressDialog?.show()

        viewModel.uploadMovie(name, description, year, cId, iUri, vUri, durations, castMovie,
            onProgress = { progress ->
                // Custom handling or update if needed, since it's AlertDialog we just keep it showing
            },
            onResult = { success, message ->
                progressDialog?.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    castMovie.clear()
                    finish()
                }
            }
        )
    }

    private fun categoryPickDialog() {
        val categories = viewModel.uiState.value.categories
        if (categories.isEmpty()) return

        val categoryNames = categories.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Pick Category")
            .setItems(categoryNames) { _, which ->
                selectedCategoryTitle = categories[which].name
                selectedCategoryId = categories[which].id
                binding.category.text = selectedCategoryTitle
            }.show()
    }

    private fun openVideoFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "video/*" }
        startActivityForResult(intent, 101)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                DATA.MIX_VIDEO_X -> pickImage(DATA.MIX_VIDEO_X)
                101 -> openVideoFiles()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data?.data != null) {
            videoUri = data.data
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(this, videoUri)
                durations = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                binding.duration.text = (durations?.toLong() ?: 0L).convertDuration()
                binding.choose.setText(R.string.ok)
            } catch (e: Exception) {
                Timber.e(e, "Metadata retrieval failed")
            } finally {
                retriever.release()
            }
        }
        if (requestCode == DATA.MIX_VIDEO_X && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                cropVideoSquare(uri)
            } else {
                val resultUri = data.getParcelableExtra<Uri>("CROP_RESULT_URI")
                if (resultUri != null) {
                    imageUri = resultUri
                    binding.image.setImageURI(imageUri)
                    binding.image.loadBlurUri(imageUri, 50)
                }
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
