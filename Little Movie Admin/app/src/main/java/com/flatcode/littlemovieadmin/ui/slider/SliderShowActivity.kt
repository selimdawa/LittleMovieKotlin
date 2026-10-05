package com.flatcode.littlemovieadmin.ui.slider

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivitySliderShowBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.ProgressDialog
import com.flatcode.littlemovieadmin.utils.cropImageSlider
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.pickImage
import com.flatcode.littlemovieadmin.utils.requestStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class SliderShowActivity : BaseActivity() {

    private lateinit var binding: ActivitySliderShowBinding
    private val viewModel: SliderShowViewModel by viewModels()
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var imageNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.slider_show)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupClickListeners()
        observeState()
    }

    private fun setupClickListeners() {
        val addButtons = listOf(
            binding.addOne,
            binding.addTwo,
            binding.addThree,
            binding.addFour,
            binding.addFive,
            binding.addSix,
            binding.addSeven,
            binding.addEight,
            binding.addNine,
            binding.addTeen,
            binding.addEleven,
            binding.addTwelfth,
            binding.addThirteen,
            binding.addFourteenth,
            binding.addFifteenth,
            binding.addSixteen,
            binding.addSeventeen,
            binding.addEighteen,
            binding.addNineteen,
            binding.addTwenty
        )

        addButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                requestStorage(DATA.MIX_SLIDER_X) {
                    pickImage(DATA.MIX_SLIDER_X)
                    imageNumber = index + 1
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == DATA.MIX_SLIDER_X) {
                pickImage(DATA.MIX_SLIDER_X)
            }
        }
    }

    private fun observeState() {
        val linearLayouts = listOf(
            binding.linearOne,
            binding.linearTwo,
            binding.linearThree,
            binding.linearFour,
            binding.linearFive,
            binding.linearSix,
            binding.linearSeven,
            binding.linearEight,
            binding.linearNine,
            binding.linearTeen,
            binding.linearEleven,
            binding.linearTwelfth,
            binding.linearThirteen,
            binding.linearFourteenth,
            binding.linearFifteenth,
            binding.linearSixteen,
            binding.linearSeventeen,
            binding.linearEighteen,
            binding.linearNineteen,
            binding.linearTwenty
        )

        val imageViews = listOf(
            binding.imageOne,
            binding.imageTwo,
            binding.imageThree,
            binding.imageFour,
            binding.imageFive,
            binding.imageSix,
            binding.imageSeven,
            binding.imageEight,
            binding.imageNine,
            binding.imageTeen,
            binding.imageEleven,
            binding.imageTwelfth,
            binding.imageThirteen,
            binding.imageFourteenth,
            binding.imageFifteenth,
            binding.imageSixteen,
            binding.imageSeventeen,
            binding.imageEighteen,
            binding.imageNineteen,
            binding.imageTwenty
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemCount.collect { itemCount ->
                    binding.toolbar.nameSpace.text =
                        MessageFormat.format("Slider Show ( {0} )", itemCount)

                    linearLayouts.forEachIndexed { index, linearLayout ->
                        linearLayout.visibility =
                            if (itemCount >= index) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { images ->
                    imageViews.forEachIndexed { index, imageView ->
                        val url = images[(index + 1).toString()]
                        imageView.loadImage(false, url)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.bar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA.MIX_SLIDER_X && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                cropImageSlider(uri)
            } else {
                val resultUri =
                    IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                if (resultUri != null) {
                    imageUri = resultUri
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        val uri = imageUri ?: return
        dialog?.setMessage("Posting photo...")
        dialog?.show()

        viewModel.uploadImage(uri, imageNumber.toString()) { _, message ->
            dialog?.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}