package com.flatcode.littlemovieadmin.ui.movie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.service.FloatingWidgetService
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.databinding.ActivityMovieViewBinding
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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

    override fun onBackPressed() {
        super.onBackPressed()
        exoPlayer?.release()
        exoPlayer = null
    }
}
