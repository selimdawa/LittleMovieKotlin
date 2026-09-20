package com.flatcode.littlemovie.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.DialogAboutAppBinding
import com.flatcode.littlemovie.databinding.DialogAboutArtistBinding
import com.flatcode.littlemovie.databinding.DialogCloseAppBinding
import com.flatcode.littlemovie.databinding.DialogLogoutBinding
import com.flatcode.littlemovie.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageContractOptions
import java.io.Serializable
import java.text.MessageFormat

object VOID {
    fun GlideImage(isUser: Boolean, context: Context, url: String?, imageView: ImageView) {
        imageView.GlideImage(isUser, url)
    }

    fun GlideBlur(isUser: Boolean, context: Context, url: String?, imageView: ImageView, level: Int) {
        imageView.GlideBlur(isUser, url, level)
    }

    fun dialogAboutArtist(context: Context, imageDB: String?, nameDB: String?, aboutDB: String?) {
        context.dialogAboutArtist(imageDB, nameDB, aboutDB)
    }

    fun isInterested(imageView: ImageView, id: String?, type: String?) {
        imageView.isInterested(id, type)
    }

    fun checkInterested(imageView: ImageView, type: String?, id: String?) {
        imageView.checkInterested(type, id)
    }

    fun incrementViewCount(id: String?) {
        id?.incrementViewCount()
    }

    fun getFileExtension(uri: Uri, context: Context): String {
        return uri.getFileExtension(context)
    }
}

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
fun ImageView.GlideImage(isUser: Boolean, url: String?) {
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
    } catch (e: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

fun ImageView.GlideBlur(isUser: Boolean, url: String?, level: Int) {
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
    } catch (e: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

fun Context.closeApp(a: Activity?) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogCloseAppBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.yes.setOnClickListener { a!!.finish() }
    binding.no.setOnClickListener { dialog.cancel() }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogLogout() {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogLogoutBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.yes.setOnClickListener {
        FirebaseAuth.getInstance().signOut()
        this.openActivity<AuthActivity>(clear = true)
    }
    binding.no.setOnClickListener { dialog.cancel() }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.setType("text/plain")
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        " Download the app now from Google Play " + " https://play.google.com/store/apps/details?id=" + this.packageName
    )
    this.startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
}

fun Context.rateApp() {
    val uri = Uri.parse("market://details?id=" + this.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        this.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
            )
        )
    }
}

fun Context.dialogAboutApp() {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogAboutAppBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.website.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View) {
            this@dialogAboutApp.startActivity(websiteIntent)
        }

        val websiteIntent: Intent get() = Intent(Intent.ACTION_VIEW, Uri.parse(DATA.WEB_SITE))
    })
    binding.facebook.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View) {
            this@dialogAboutApp.startActivity(openFacebookIntent)
        }

        val openFacebookIntent: Intent
            get() = try {
                this@dialogAboutApp.packageManager.getPackageInfo("com.facebook.katana", 0)
                Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + DATA.FB_ID))
            } catch (e: Exception) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/" + DATA.FB_ID)
                )
            }
    })
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Uri.getFileExtension(context: Context): String {
    val cR: ContentResolver = context.contentResolver
    val mime: MimeTypeMap = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this))!!
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogAboutArtistBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.image.GlideImage(false, imageDB)
    binding.name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB))
    binding.aboutTheArtist.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB))
    dialog.show()
    dialog.window!!.attributes = lp
}

fun ImageView.isInterested(id: String?, type: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
    ref.child(DATA.FirebaseUserUid).child(type!!).child(id!!)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    this@isInterested.setImageResource(R.drawable.ic_star_selected)
                    this@isInterested.tag = "added"
                } else {
                    this@isInterested.setImageResource(R.drawable.ic_star_unselected)
                    this@isInterested.tag = "add"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
}

fun ImageView.checkInterested(type: String?, id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).setValue(true)
        incrementInterestedCount(id, type, 1)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).removeValue()
        incrementInterestedCount(id, type, -1)
    }
}

private fun incrementInterestedCount(id: String?, type: String?, increment: Int) {
    val ref = FirebaseDatabase.getInstance().getReference(type!!).child(id!!)
        .child(DATA.INTERESTED_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var count = 0
            if (snapshot.exists()) {
                count = snapshot.value.toString().toInt()
            }
            ref.setValue(count + increment)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
    ref.child(userId!!).child(id!!).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                this@isFavorite.setImageResource(R.drawable.ic_heart_selected)
                this@isFavorite.tag = "added"
            } else {
                this@isFavorite.setImageResource(R.drawable.ic_heart_unselected)
                this@isFavorite.tag = "add"
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).setValue(true)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).removeValue()
    }
}

fun ImageView.isLoves(id: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
    ref.child(DATA.FirebaseUserUid).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                this@isLoves.setImageResource(R.drawable.ic_heart_selected)
                this@isLoves.tag = "added"
            } else {
                this@isLoves.setImageResource(R.drawable.ic_heart_unselected)
                this@isLoves.tag = "add"
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun TextView.nrLoves(id: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format("{0}", snapshot.childrenCount)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkLove(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).setValue(true)
        incrementLovesCount(id, 1)
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).removeValue()
        incrementLovesCount(id, -1)
    }
}

private fun incrementLovesCount(id: String?, increment: Int) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(id!!)
        .child(DATA.LOVES_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var count = 0
            if (snapshot.exists()) {
                count = snapshot.value.toString().toInt()
            }
            ref.setValue(count + increment)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementViewCount() {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(this)
        .child(DATA.VIEWS_COUNT)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = 0
            if (snapshot.exists()) {
                viewsCount = snapshot.value.toString().toInt()
            }
            ref.setValue(viewsCount + 1)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format("%d:%02d", minutes, seconds)
}

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

fun Long.getTimeAgo(): String? {
    var time = this
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "just now"
        diff < 2 * MINUTE_MILLIS -> "a minute ago"
        diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 90 * MINUTE_MILLIS -> "an hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

fun Long.getMessageAgo(): String? {
    var time = this
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "1 s"
        diff < 2 * MINUTE_MILLIS -> "1 m"
        diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} m"
        diff < 90 * MINUTE_MILLIS -> "1 h"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} h"
        diff < 48 * HOUR_MILLIS -> "1 d"
        else -> "${diff / DAY_MILLIS} d"
    }
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
