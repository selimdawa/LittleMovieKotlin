package com.flatcode.littlemovie.ui.movie

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.flatcode.littlemovie.databinding.ActivityMovieViewBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.incrementViewCount
import dagger.hilt.android.AndroidEntryPoint

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MovieViewActivity : BaseActivity() {

    private var binding: ActivityMovieViewBinding? = null
    var activity: Activity = this@MovieViewActivity
    var videoUri: Uri? = null
    var exoPlayer: ExoPlayer? = null
    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMovieViewBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val intent = intent
        if (intent != null) {
            val uriValue = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = uriValue?.toUri()
            id = intent.getStringExtra(DATA.MOVIE_ID)
            id?.incrementViewCount()
        }
        val trackSelector = DefaultTrackSelector(this)
        exoPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playVideo()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    exoPlayer?.playWhenReady = false
                    exoPlayer?.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                exoPlayer = null
                finish()
            }
        })
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun playVideo() {
        try {
            val uri = videoUri ?: return
            val mediaItem = MediaItem.fromUri(uri)
            binding!!.playerView.player = exoPlayer
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            exoPlayer?.playWhenReady = false
            exoPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        exoPlayer = null
    }
}
