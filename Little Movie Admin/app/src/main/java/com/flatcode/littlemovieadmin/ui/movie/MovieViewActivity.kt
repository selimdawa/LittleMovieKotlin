package com.flatcode.littlemovieadmin.ui.movie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityMovieViewBinding
import com.flatcode.littlemovieadmin.service.FloatingWidgetService
import com.flatcode.littlemovieadmin.ui.BaseActivity
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

        // Access view from sub-layout
        binding.playerView.findViewById<ImageView>(R.id.exo_floating_widget)?.setOnClickListener {
            exoPlayer?.let {
                it.playWhenReady = false
                it.release()
            }
            val service = Intent(this, FloatingWidgetService::class.java)
            service.putExtra(DATA.MOVIE_LINK, videoUri.toString())
            startService(service)
        }

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
