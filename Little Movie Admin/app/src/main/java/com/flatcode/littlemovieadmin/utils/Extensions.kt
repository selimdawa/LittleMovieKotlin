package com.flatcode.littlemovieadmin.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.ui.cast.CastEditActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryEditActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieEditActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import java.io.Serializable
import java.text.MessageFormat

inline fun <reified T : Activity> Context.openActivity(
    clear: Boolean = false,
    vararg extras: Pair<String, Any?>
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

fun ImageView.loadGlideImage(url: String?, isUser: Boolean = false) {
    try {
        if (url == DATA.BASIC || url.isNullOrEmpty()) {
            if (isUser) this.setImageResource(R.drawable.basic_user) else this.setImageResource(
                R.drawable.basic_music
            )
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                error(if (isUser) R.drawable.basic_user else R.drawable.basic_music)
                crossfade(true)
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "GlideImage error")
        this.setImageResource(R.drawable.basic_music)
    }
}

fun ImageView.loadGlideBlur(url: String?, level: Int, isUser: Boolean = false) {
    try {
        if (url == DATA.BASIC || url.isNullOrEmpty()) {
            if (isUser) this.setImageResource(R.drawable.basic_user) else this.setImageResource(
                R.drawable.basic_music
            )
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                transformations(SimpleBlurTransformation(level.toFloat()))
                error(if (isUser) R.drawable.basic_user else R.drawable.basic_music)
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "GlideBlur error")
        this.setImageResource(R.drawable.basic_music)
    }
}

fun ImageView.loadGlideBlurUri(uri: Uri?, level: Int) {
    if (uri != null) {
        this.load(uri) {
            placeholder(R.color.image_profile)
            transformations(SimpleBlurTransformation(level.toFloat()))
        }
    }
}

fun incrementItemCount(database: String?, id: String?, childDB: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(database!!)
    ref.child(id!!).child(childDB!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentCount = snapshot.getValue(Long::class.java) ?: 0L
            ref.child(id).child(childDB).setValue(currentCount + 1)
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "incrementItemCount cancelled")
        }
    })
}

fun incrementItemRemoveCount(database: String?, id: String?, childDB: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(database!!)
    ref.child(id!!).child(childDB!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentCount = snapshot.getValue(Long::class.java) ?: 0L
            if (currentCount > 0) {
                ref.child(id).child(childDB).setValue(currentCount - 1)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "incrementItemRemoveCount cancelled")
        }
    })
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

fun Activity.pickImage(requestCode: Int) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
}

fun Activity.cropImageSquare(uri: Uri) {
    val intent = Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", 1)
        putExtra("ASPECT_RATIO_Y", 1)
        putExtra("IS_OVAL", true)
        putExtra("MIN_WIDTH", DATA.MIX_SQUARE)
        putExtra("MIN_HEIGHT", DATA.MIX_SQUARE)
    }
    startActivityForResult(intent, DATA.MIX_SQUARE)
}

fun Activity.cropVideoSquare(uri: Uri) {
    val intent = Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", 10)
        putExtra("ASPECT_RATIO_Y", 14)
        putExtra("IS_OVAL", true)
        putExtra("MIN_WIDTH", DATA.MIX_VIDEO_X)
        putExtra("MIN_HEIGHT", DATA.MIX_VIDEO_Y)
    }
    startActivityForResult(intent, DATA.MIX_VIDEO_X)
}

fun Activity.cropImageSlider(uri: Uri) {
    val intent = Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", 16)
        putExtra("ASPECT_RATIO_Y", 9)
        putExtra("IS_OVAL", true)
        putExtra("MIN_WIDTH", DATA.MIX_SLIDER_X)
        putExtra("MIN_HEIGHT", DATA.MIX_SLIDER_Y)
    }
    startActivityForResult(intent, DATA.MIX_SLIDER_X)
}

fun Context.getFileExtension(uri: Uri?): String? {
    val cR = this.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri!!))
}

fun Activity.moreDeleteCategory(
    item: Category?,
    DB: String?,
    idDB: String?,
    childDB: String?,
    cast: Boolean?,
    movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { dialog: DialogInterface?, which: Int ->
            if (which == 0) {
                this.openActivity<CategoryEditActivity>(extras = arrayOf(DATA.CATEGORY_ID to id))
            } else if (which == 1) {
                this.dialogOptionDelete(
                    id, name, DATA.CATEGORY, DATA.CATEGORIES,
                    false, DB, idDB, childDB, cast, movie,
                )
            }
        }.show()
}

fun Activity.moreDeleteCast(
    item: Cast?, DB: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { dialog: DialogInterface?, which: Int ->
            if (which == 0) {
                this.openActivity<CastEditActivity>(extras = arrayOf(DATA.CAST_ID to id))
            } else if (which == 1) {
                this.dialogOptionDelete(
                    id, name, DATA.CAST, DATA.CAST,
                    false, DB, idDB, childDB, cast, movie,
                )
            }
        }.show()
}

fun Activity.moreDeleteMovie(
    item: Movie?, DB: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val categoryId = DATA.EMPTY + item.categoryId
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { dialog: DialogInterface?, which: Int ->
            if (which == 0) {
                this.openActivity<MovieEditActivity>(
                    extras = arrayOf(
                        DATA.MOVIE_ID to id, DATA.CATEGORY_ID to categoryId
                    )
                )
            } else if (which == 1) {
                this.dialogOptionDelete(
                    id, name, DATA.MOVIE, DATA.MOVIES,
                    false, DB, idDB, childDB, cast, movie,
                )
            }
        }.show()
}

fun Activity.dialogOptionDelete(
    id: String?, name: String, type: String?, nameDB: String?,
    isEditorsChoice: Boolean, DB: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_logout)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    val title = dialog.findViewById<TextView>(R.id.title)
    title.text = "Do you want to delete $name ( $type ) ?"

    dialog.findViewById<View>(R.id.yes).setOnClickListener {
        if (isEditorsChoice) this.dialogUpdateEditorsChoice(dialog, id) else this.deleteDB(
            dialog, id, name, nameDB, DB, idDB, childDB
        )
        if (cast == true) deleteCastInfo(id!!) else if (movie == true) deleteMovieInfo(id!!)
    }

    dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.dismiss() }
    dialog.show()
    dialog.window!!.attributes = lp
}

private fun deleteMovieInfo(id: String) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE).child(id)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                incrementItemRemoveCount(DATA.CAST, snapshot.key, DATA.MOVIES_COUNT)
            }
            ref.removeValue()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

private fun deleteCastInfo(id: String) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                if (snapshot.hasChild(id)) {
                    ref.child(snapshot.key!!).child(id).removeValue()
                    incrementItemRemoveCount(DATA.MOVIES, snapshot.key, DATA.CAST_COUNT)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun Context.dialogUpdateEditorsChoice(dialogDelete: Dialog, id: String?) {
    val dialog = ProgressDialog(this)
    dialog.setMessage("Updating Editors Choice...")
    dialog.show()
    val hashMap = HashMap<String, Any>()
    hashMap[DATA.EDITORS_CHOICE] = 0

    val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
    reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
        dialog.dismiss()
        Toast.makeText(this, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }
}

fun Activity.deleteDB(
    dialogDelete: Dialog, id: String?, name: String, nameDB: String?,
    DB: String?, idDB: String?, childDB: String?,
) {
    val dialog = ProgressDialog(this)
    dialog.setTitle("Please wait")
    dialog.setMessage("Deleting $name ...")
    dialog.show()
    val reference = FirebaseDatabase.getInstance().getReference(nameDB!!)
    reference.child(id!!).removeValue().addOnSuccessListener {
        if ((DB != null) and (idDB != null) and (childDB != null)) incrementItemRemoveCount(
            DB,
            idDB,
            childDB
        )
        DATA.isChange = true
        this.onBackPressed()
        dialog.dismiss()
        Toast.makeText(this, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.addToEditorsChoice(activity: Activity, id: String?, number: Int) {
    val dialog = ProgressDialog(this)
    dialog.setMessage("Updating Editors Choice...")
    dialog.show()
    val hashMap = HashMap<String, Any>()
    hashMap[DATA.EDITORS_CHOICE] = number
    val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
    reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
        dialog.dismiss()
        Toast.makeText(this, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
        activity.finish()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_about_artist)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    val image = dialog.findViewById<ImageView>(R.id.image)
    val name = dialog.findViewById<TextView>(R.id.name)
    val aboutTheArtist = dialog.findViewById<TextView>(R.id.aboutTheArtist)

    image.loadGlideImage(imageDB, false)
    name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
    aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format("%d:%02d", minutes, seconds)
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

// GetTimeAgo
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