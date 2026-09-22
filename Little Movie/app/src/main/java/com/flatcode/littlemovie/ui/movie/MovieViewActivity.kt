package com.flatcode.littlemovie.ui.movie

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.*
import com.flatcode.littlemovie.databinding.ActivityMovieViewBinding
import com.flatcode.littlemovie.service.FloatingWidgetService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint

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
            v.updatePadding(left = systemBars.left, right = systemBars.right, bottom = systemBars.bottom)
            insets
        }

        val intent = intent
        if (intent != null) {
            val uriValue = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = Uri.parse(uriValue)
            id = intent.getStringExtra(DATA.MOVIE_ID)
            id?.incrementViewCount()
        }
        binding!!.playerView.findViewById<ImageView>(R.id.exo_floating_widget).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                Toast.makeText(this, "Please allow drawing over other apps", Toast.LENGTH_SHORT).show()
            } else {
                startFloatingService()
            }
        }
        val trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())
        exoPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playVideo()
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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun playVideo() {
        try {
            val playerInfo = Util.getUserAgent(this, "MovieAppClient")
            val dataSourceFactory = DefaultDataSourceFactory(this, playerInfo)
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri!!))
            binding!!.playerView.player = exoPlayer
            exoPlayer!!.prepare(mediaSource)
            exoPlayer!!.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer!!.playWhenReady = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exoPlayer!!.playWhenReady = false
        exoPlayer!!.release()
    }
}
