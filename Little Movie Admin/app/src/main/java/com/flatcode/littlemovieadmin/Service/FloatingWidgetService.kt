package com.flatcode.littlemovieadmin.service

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
import com.flatcode.littlemovieadmin.ui.movie.MovieViewActivity
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class FloatingWidgetService : Service() {

    var windowManager: WindowManager? = null
    private var FloatingWidget: View? = null
    var videoUri: Uri? = null
    var exoPlayer: ExoPlayer? = null
    var playerView: PlayerView? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null) {
            val uriStr = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = Uri.parse(uriStr)
            if (windowManager != null && FloatingWidget!!.isShown && exoPlayer != null) {
                windowManager!!.removeView(FloatingWidget)
                FloatingWidget = null
                windowManager = null
                exoPlayer!!.playWhenReady = false
                exoPlayer!!.release()
                exoPlayer = null
            }
            val params: WindowManager.LayoutParams
            FloatingWidget = LayoutInflater.from(this).inflate(R.layout.item_pop_up_window, null)
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
            windowManager!!.addView(FloatingWidget, params)
            
            exoPlayer = ExoPlayer.Builder(this).build()
            playerView = FloatingWidget!!.findViewById(R.id.playerView)
            val close = FloatingWidget!!.findViewById<ImageView>(R.id.close)
            val maximize = FloatingWidget!!.findViewById<ImageView>(R.id.maximize)

            maximize.setOnClickListener {
                if (windowManager != null && FloatingWidget!!.isShown() && exoPlayer != null) {
                    windowManager!!.removeView(FloatingWidget)
                    FloatingWidget = null
                    windowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()
                    this@FloatingWidgetService.openActivity<MovieViewActivity>(
                        clear = true,
                        extras = arrayOf(DATA.MOVIE_LINK to videoUri.toString())
                    )
                }
            }
            close.setOnClickListener {
                if (windowManager != null && FloatingWidget!!.isShown() && exoPlayer != null) {
                    windowManager!!.removeView(FloatingWidget)
                    FloatingWidget = null
                    windowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()
                }
            }
            playVideos()
            FloatingWidget!!.findViewById<View>(R.id.item)
                .setOnTouchListener(object : OnTouchListener {
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
                                windowManager!!.updateViewLayout(FloatingWidget, params)
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
            exoPlayer = ExoPlayer.Builder(this).build()
            val mediaItem = MediaItem.fromUri(videoUri!!)
            playerView!!.setPlayer(exoPlayer)
            exoPlayer!!.setMediaItem(mediaItem)
            exoPlayer!!.prepare()
            exoPlayer!!.setPlayWhenReady(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (FloatingWidget != null) windowManager!!.removeView(FloatingWidget)
    }
}
