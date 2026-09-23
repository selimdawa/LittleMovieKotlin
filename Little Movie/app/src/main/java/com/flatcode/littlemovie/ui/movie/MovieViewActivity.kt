package com.flatcode.littlemovie.ui.movie

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityMovieViewBinding
import com.flatcode.littlemovie.service.FloatingWidgetService
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.incrementViewCount
import dagger.hilt.android.AndroidEntryPoint

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MovieViewActivity : AppCompatActivity() {

    private var binding: ActivityMovieViewBinding? = null
    var activity: Activity = this@MovieViewActivity
    var videoUri: Uri? = null
    var exoPlayer: ExoPlayer? = null
    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setFullScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMovieViewBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left, right = systemBars.right, bottom = systemBars.bottom
            )
            insets
        }

        val intent = intent
        if (intent != null) {
            val uriValue = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = uriValue?.toUri()
            id = intent.getStringExtra(DATA.MOVIE_ID)
            id?.incrementViewCount()
        }
        binding!!.playerView.findViewById<ImageView>(R.id.exo_floating_widget).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri()
                )
                startActivity(intent)
                Toast.makeText(this, "Please allow drawing over other apps", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startFloatingService()
            }
        }
        val trackSelector = DefaultTrackSelector(this)
        exoPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playVideo()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exoPlayer?.playWhenReady = false
                exoPlayer?.release()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    private fun startFloatingService() {
        exoPlayer!!.playWhenReady = false
        exoPlayer!!.release()
        val service = Intent(activity, FloatingWidgetService::class.java)
        service.putExtra(DATA.MOVIE_LINK, videoUri.toString())
        service.putExtra(DATA.MOVIE_ID, id)
        startService(service)
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
            val mediaItem = MediaItem.fromUri(videoUri!!)
            binding!!.playerView.player = exoPlayer
            exoPlayer!!.setMediaItem(mediaItem)
            exoPlayer!!.prepare()
            exoPlayer!!.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer!!.playWhenReady = false
    }


}
