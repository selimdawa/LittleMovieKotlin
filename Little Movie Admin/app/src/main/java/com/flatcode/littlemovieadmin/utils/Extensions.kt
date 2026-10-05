package com.flatcode.littlemovieadmin.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemovieadmin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.text.MessageFormat
import java.util.Locale

inline fun <reified T : Activity> Context.openActivity(
    clear: Boolean = false, vararg extras: Pair<String, Any?>
) {
    val intent = Intent(this, T::class.java).apply {
        if (clear) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Long -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Serializable -> putExtra(key, value)
            }
        }
    }
    startActivity(intent)
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            ?: return false
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Activity.cropImage(
    uri: Uri,
    aspectRatioX: Int = 1,
    aspectRatioY: Int = 1,
    isOval: Boolean = false,
    minWidth: Int = DATA.MIX_SQUARE,
    minHeight: Int = DATA.MIX_SQUARE,
    requestCode: Int = DATA.MIX_SQUARE
) {
    val intent = Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", aspectRatioX)
        putExtra("ASPECT_RATIO_Y", aspectRatioY)
        putExtra("IS_OVAL", isOval)
        putExtra("MIN_WIDTH", minWidth)
        putExtra("MIN_HEIGHT", minHeight)
    }
    startActivityForResult(intent, requestCode)
}

fun ImageView.loadImage(isUser: Boolean, data: Any? = null, url: Any? = null) {
    val imageSource = data ?: url
    val defaultRes = if (isUser) R.drawable.basic_user else R.color.image_profile
    try {
        val str = imageSource?.toString()
        if (imageSource == null || str.isNullOrBlank() || str == DATA.BASIC || str == "null") {
            this.setImageResource(defaultRes)
        } else {
            this.load(imageSource) {
                placeholder(R.color.image_profile)
                error(defaultRes)
                fallback(defaultRes)
                crossfade(true)
            }
        }
    } catch (_: Exception) {
        this.setImageResource(defaultRes)
    }
}

fun ImageView.loadBlur(isUser: Boolean, url: String?, level: Int) {
    val defaultRes = if (isUser) R.drawable.basic_user else R.color.image_profile
    try {
        if (url.isNullOrBlank() || url == DATA.BASIC || url == "null") {
            this.setImageResource(defaultRes)
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                error(defaultRes)
                fallback(defaultRes)
                transformations(SimpleBlurTransformation(level.toFloat()))
            }
        }
    } catch (_: Exception) {
        this.setImageResource(defaultRes)
    }
}

fun ImageView.loadBlurUri(uri: Uri?, level: Int) {
    if (uri != null) {
        this.load(uri) {
            placeholder(R.color.image_profile)
            error(R.color.image_profile)
            transformations(SimpleBlurTransformation(level.toFloat()))
        }
    } else {
        this.setImageResource(R.color.image_profile)
    }
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    if (id == null || userId == null) return
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(userId)
    reference.child(id).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                this@isFavorite.setImageResource(R.drawable.ic_star_selected)
                this@isFavorite.tag = "added"
            } else {
                this@isFavorite.setImageResource(R.drawable.ic_star_unselected)
                this@isFavorite.tag = "add"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (id == null) return
    val ref =
        FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
            .child(id)
    if (this.tag == "add") {
        ref.setValue(true)
    } else {
        ref.removeValue()
    }
}

fun TextView.nrLoves(id: String?) {
    if (id == null) return
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun Context.checkStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.requestStoragePermission(requestCode: Int) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

fun Context.checkVideoPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.requestVideoPermission(requestCode: Int) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

fun Activity.requestStorage(requestCode: Int, onGranted: () -> Unit) {
    if (checkStoragePermission()) {
        onGranted()
    } else {
        requestStoragePermission(requestCode)
    }
}

fun Activity.requestVideo(requestCode: Int, onGranted: () -> Unit) {
    if (checkVideoPermission()) {
        onGranted()
    } else {
        requestVideoPermission(requestCode)
    }
}

fun Activity.pickImage(requestCode: Int) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun TextView.loadCategory(categoryId: String?) {
    if (categoryId == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
    ref.child(categoryId).child(DATA.NAME)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                this@loadCategory.text = snapshot.value?.toString() ?: ""
            }

            override fun onCancelled(error: DatabaseError) {}
        })
}

// SimpleBlurTransformation
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
