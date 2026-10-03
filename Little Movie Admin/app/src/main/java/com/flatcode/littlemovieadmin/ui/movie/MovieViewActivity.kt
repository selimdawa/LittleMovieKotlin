package com.flatcode.littlemovieadmin.ui.movie

import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.flatcode.littlemovieadmin.databinding.ActivityMovieViewBinding
import com.flatcode.littlemovieadmin.utils.BaseActivity
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieViewActivity : BaseActivity() {

    private lateinit var binding: ActivityMovieViewBinding
    private var exoPlayer: ExoPlayer? = null
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMovieViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriValue = intent.getStringExtra(DATA.MOVIE_LINK)
        if (uriValue != null) {
            videoUri = uriValue.toUri()
        }

        initializePlayer()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                exoPlayer?.release()
                exoPlayer = null
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        })
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun initializePlayer() {
        videoUri?.let { uri ->
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                binding.playerView.player = this
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
        exoPlayer = null
    }

}