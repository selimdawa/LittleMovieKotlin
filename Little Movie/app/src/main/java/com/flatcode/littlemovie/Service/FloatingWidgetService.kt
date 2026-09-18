package com.flatcode.littlemovie.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.flatcode.littlemovie.ui.movie.MovieViewActivity
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.utils.DATA.MOVIE_ID
import com.flatcode.littlemovie.utils.DATA.MOVIE_LINK
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.flatcode.littlemovie.databinding.ItemPopUpWindowBinding

class FloatingWidgetService : Service() {

    var windowManager: WindowManager? = null
    private var binding: ItemPopUpWindowBinding? = null
    var videoUri: Uri? = null
    var exoPlayer: SimpleExoPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val uriStr = intent.getStringExtra(MOVIE_LINK)
            val id = intent.getStringExtra(MOVIE_ID)
            videoUri = Uri.parse(uriStr)
            if (windowManager != null && binding != null && binding!!.root.isShown && exoPlayer != null) {
                windowManager!!.removeView(binding!!.root)
                binding = null
                windowManager = null
                exoPlayer!!.playWhenReady = false
                exoPlayer!!.release()
                exoPlayer = null
            }
            val params: WindowManager.LayoutParams
            binding = ItemPopUpWindowBinding.inflate(LayoutInflater.from(this))
            params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            } else {
                WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            }
            params.gravity = Gravity.TOP or Gravity.LEFT
            params.x = 200
            params.y = 200
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager!!.addView(binding!!.root, params)
            val trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())
            exoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()

            binding!!.maximize.setOnClickListener {
                if (windowManager != null && binding != null && binding!!.root.isShown && exoPlayer != null) {
                    windowManager!!.removeView(binding!!.root)
                    binding = null
                    windowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()
                    val intent1 = Intent(
                        this@FloatingWidgetService, MovieViewActivity::class.java
                    )
                    intent1.putExtra(MOVIE_LINK, videoUri.toString())
                    intent1.putExtra(MOVIE_ID, id)
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent1)
                }
            }
            binding!!.close.setOnClickListener {
                if (windowManager != null && binding != null && binding!!.root.isShown && exoPlayer != null) {
                    windowManager!!.removeView(binding!!.root)
                    binding = null
                    windowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()
                }
            }
            playVideos()
            binding!!.item.setOnTouchListener(object : OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }

                        MotionEvent.ACTION_UP -> return true
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager!!.updateViewLayout(binding!!.root, params)
                            return true
                        }
                    }
                    return false
                }
            })
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun playVideos() {
        try {
            val trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())
            exoPlayer = SimpleExoPlayer.Builder(this@FloatingWidgetService)
                .setTrackSelector(trackSelector)
                .build()
            val playerInfo = Util.getUserAgent(this, "VideoPlayer")
            val dataSourceFactory = DefaultDataSourceFactory(this, playerInfo)
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri!!))
            binding!!.playerView.setPlayer(exoPlayer)
            exoPlayer!!.prepare(mediaSource)
            exoPlayer!!.setPlayWhenReady(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null) windowManager!!.removeView(binding!!.root)
    }
}
