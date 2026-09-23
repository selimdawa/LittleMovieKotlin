package com.flatcode.littlemovie.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.ImageView
import java.util.Locale
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemovie.R

inline fun <reified T : Activity> Context.openActivity(
    vararg extras: Pair<String, Any?>,
    clear: Boolean = false,
) {
    val intent = Intent(this, T::class.java).apply {
        if (clear) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
            }
        }
    }
    startActivity(intent)
}

fun ImageView.loadImage(isUser: Boolean, url: String?) {
    try {
        if (url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_music)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                crossfade(true)
            }
        }
    } catch (_: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

fun ImageView.loadImageBlur(isUser: Boolean, url: String?, level: Int) {
    try {
        if (url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_music)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                transformations(SimpleBlurTransformation(level.toFloat()))
            }
        }
    } catch (_: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

fun Context.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        " Download the app now from Google Play " + " https://play.google.com/store/apps/details?id=" + this.packageName
    )
    this.startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
}

fun Context.rateApp() {
    val uri = ("market://details?id=" + this.packageName).toUri()
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        this.startActivity(goToMarket)
    } catch (_: ActivityNotFoundException) {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                ("http://play.google.com/store/apps/details?id=" + this.packageName).toUri()
            )
        )
    }
}

fun Uri.getFileExtension(context: Context): String {
    val cR: ContentResolver = context.contentResolver
    val mime: MimeTypeMap = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this))!!
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = (this / 1000) % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

class SimpleBlurTransformation(private val radius: Float) : Transformation() {
    override val cacheKey: String = "${SimpleBlurTransformation::class.java.name}-$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        if (input.isRecycled) return input
        val scaleFactor = 6
        val w = (input.width / scaleFactor).coerceAtLeast(1)
        val h = (input.height / scaleFactor).coerceAtLeast(1)
        val small = input.scale(w, h, true)
        val r = (radius / scaleFactor).toInt().coerceAtLeast(1)
        val pix = IntArray(w * h)
        small.getPixels(pix, 0, w, 0, 0, w, h)
        val blurred = IntArray(w * h)
        for (y in 0 until h) for (x in 0 until w) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val xi = (x + i).coerceIn(0, w - 1)
                val p = pix[y * w + xi]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            blurred[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        for (x in 0 until w) for (y in 0 until h) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val yi = (y + i).coerceIn(0, h - 1)
                val p = blurred[yi * w + x]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            pix[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(pix, 0, w, 0, 0, w, h)
        val finalOutput = output.scale(input.width, input.height, true)
        if (output != finalOutput) output.recycle()
        if (small != input) small.recycle()
        return finalOutput
    }
}
