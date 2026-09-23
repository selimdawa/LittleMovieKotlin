package com.flatcode.littlemovie.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.net.toUri
import com.flatcode.littlemovie.databinding.ItemPopUpWindowBinding
import com.flatcode.littlemovie.ui.movie.MovieViewActivity
import com.flatcode.littlemovie.utils.DATA.MOVIE_ID
import com.flatcode.littlemovie.utils.DATA.MOVIE_LINK
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var binding: ItemPopUpWindowBinding? = null
    private var videoUri: Uri? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val uriStr = intent.getStringExtra(MOVIE_LINK)
            val id = intent.getStringExtra(MOVIE_ID)
            videoUri = uriStr?.toUri()

            releasePlayer()

            binding = ItemPopUpWindowBinding.inflate(LayoutInflater.from(this))
            val params = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT,
            )
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 200
            params.y = 200

            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            binding?.root?.let { windowManager?.addView(it, params) }

            binding?.maximize?.setOnClickListener {
                releasePlayer()
                stopSelf()
                val intent1 =
                    Intent(this@FloatingWidgetService, MovieViewActivity::class.java).apply {
                        putExtra(MOVIE_LINK, videoUri.toString())
                        putExtra(MOVIE_ID, id)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                startActivity(intent1)
            }

            binding?.close?.setOnClickListener {
                releasePlayer()
                stopSelf()
            }

            playVideos()

            binding?.item?.setOnTouchListener(object : View.OnTouchListener {
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
                            binding?.root?.let { windowManager?.updateViewLayout(it, params) }
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
        val uri = videoUri ?: return
        try {
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(this).build()
            }
            val mediaItem = MediaItem.fromUri(uri)
            binding?.playerView?.player = exoPlayer
            exoPlayer?.run {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releasePlayer() {
        if (windowManager != null && binding != null && binding?.root?.isShown == true) {
            windowManager?.removeView(binding?.root)
        }
        binding = null
        windowManager = null
        exoPlayer?.let { player ->
            player.playWhenReady = false
            player.release()
        }
        exoPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}