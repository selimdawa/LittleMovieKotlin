package com.flatcode.littlemovieadmin.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityMovieViewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MovieViewActivity : AppCompatActivity() {

    private var binding: ActivityMovieViewBinding? = null
    var activity: Activity = this@MovieViewActivity
    var videoUri: Uri? = null
    var exoPlayer: ExoPlayer? = null
    var extractorsFactory: ExtractorsFactory? = null
    var exo_floating_widget: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen()
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityMovieViewBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        exo_floating_widget = findViewById(R.id.exo_floating_widget)
        val intent = intent
        if (intent != null) {
            val uriValue = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = Uri.parse(uriValue)
        }
        exo_floating_widget!!.setOnClickListener {
            exoPlayer!!.playWhenReady = false
            exoPlayer!!.release()
            val service = Intent(activity, CLASS.SERVICE)
            service.putExtra(DATA.MOVIE_LINK, videoUri.toString())
            startService(service)
        }
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        extractorsFactory = DefaultExtractorsFactory()
        playVideo()
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
            val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri, dataSourceFactory, extractorsFactory, null, null
            )
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