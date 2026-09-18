package com.flatcode.littlemovieadmin.ui.slider

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.VOID
import com.flatcode.littlemovieadmin.databinding.ActivitySliderShowBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class SliderShowActivity : BaseActivity() {

    private lateinit var binding: ActivitySliderShowBinding
    private val viewModel: SliderShowViewModel by viewModels()
    private var imageUri: Uri? = null
    private var progressDialog: ProgressDialog? = null
    private var imageNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.slider_show)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        
        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        setupClickListeners()
        observeState()
    }

    private fun setupClickListeners() {
        val addButtons = listOf(
            binding.addOne, binding.addTwo, binding.addThree, binding.addFour, binding.addFive,
            binding.addSix, binding.addSeven, binding.addEight, binding.addNine, binding.addTeen,
            binding.addEleven, binding.addTwelfth, binding.addThirteen, binding.addFourteenth,
            binding.addFifteenth, binding.addSixteen, binding.addSeventeen, binding.addEighteen,
            binding.addNineteen, binding.addTwenty
        )

        addButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                VOID.CropImageSlider(this)
                imageNumber = index + 1
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.bar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.toolbar.nameSpace.text = MessageFormat.format("Slider Show ( {0} )", state.itemCount)

                    val linears = listOf(
                        binding.linearOne, binding.linearTwo, binding.linearThree, binding.linearFour, binding.linearFive,
                        binding.linearSix, binding.linearSeven, binding.linearEight, binding.linearNine, binding.linearTeen,
                        binding.linearEleven, binding.linearTwelfth, binding.linearThirteen, binding.linearFourteenth,
                        binding.linearFifteenth, binding.linearSixteen, binding.linearSeventeen, binding.linearEighteen,
                        binding.linearNineteen, binding.linearTwenty
                    )

                    val imageViews = listOf(
                        binding.imageOne, binding.imageTwo, binding.imageThree, binding.imageFour, binding.imageFive,
                        binding.imageSix, binding.imageSeven, binding.imageEight, binding.imageNine, binding.imageTeen,
                        binding.imageEleven, binding.imageTwelfth, binding.imageThirteen, binding.imageFourteenth,
                        binding.imageFifteenth, binding.imageSixteen, binding.imageSeventeen, binding.imageEighteen,
                        binding.imageNineteen, binding.imageTwenty
                    )

                    linears.forEachIndexed { index, linearLayout ->
                        linearLayout.visibility = if (state.itemCount >= index) View.VISIBLE else View.GONE
                    }

                    imageViews.forEachIndexed { index, imageView ->
                        val url = state.images[(index + 1).toString()]
                        VOID.GlideImage(false, this@SliderShowActivity, url, imageView)
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
                VOID.CropImageSlider(this)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                uploadImage()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error! ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage() {
        val uri = imageUri ?: return
        progressDialog?.setMessage("Posting photo...")
        progressDialog?.show()
        
        val extension = VOID.getFileExtension(uri, this)
        viewModel.uploadImage(uri, imageNumber.toString(), extension) { success, message ->
            progressDialog?.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSliderShow()
    }
}
