package com.flatcode.littlemovieadmin.service

import android.annotation.SuppressLint
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
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.ui.movie.MovieViewActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.openActivity
import timber.log.Timber

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingWidget: View? = null
    private var videoUri: Uri? = null
    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val uriStr = intent.getStringExtra(DATA.MOVIE_LINK)
            videoUri = uriStr?.toUri()

            releasePlayerAndWidget()

            val widgetView = LayoutInflater.from(this).inflate(R.layout.item_pop_up_window, null)
            floatingWidget = widgetView

            @Suppress("DEPRECATION") val params =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 200
            params.y = 200

            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager = wm
            wm.addView(widgetView, params)

            playerView = widgetView.findViewById(R.id.playerView)
            val close = widgetView.findViewById<ImageView>(R.id.close)
            val maximize = widgetView.findViewById<ImageView>(R.id.maximize)

            maximize.setOnClickListener {
                releasePlayerAndWidget()
                stopSelf()
                openActivity<MovieViewActivity>(
                    clear = true, extras = arrayOf(DATA.MOVIE_LINK to videoUri.toString())
                )
            }

            close.setOnClickListener {
                releasePlayerAndWidget()
                stopSelf()
            }

            playVideos()

            widgetView.findViewById<View>(R.id.item)
                ?.setOnTouchListener(object : View.OnTouchListener {
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

                            MotionEvent.ACTION_UP -> {
                                view.performClick()
                                return true
                            }

                            MotionEvent.ACTION_MOVE -> {
                                params.x = initialX + (event.rawX - initialTouchX).toInt()
                                params.y = initialY + (event.rawY - initialTouchY).toInt()
                                windowManager?.updateViewLayout(floatingWidget, params)
                                return true
                            }
                        }
                        return false
                    }
                })
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun playVideos() {
        try {
            val uri = videoUri ?: return
            val player = ExoPlayer.Builder(this).build()
            exoPlayer = player
            playerView?.player = player
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun releasePlayerAndWidget() {
        if (floatingWidget != null && floatingWidget?.isShown == true && windowManager != null) {
            windowManager?.removeView(floatingWidget)
            floatingWidget = null
            windowManager = null
        }
        exoPlayer?.let {
            it.playWhenReady = false
            it.release()
        }
        exoPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayerAndWidget()
    }
}
