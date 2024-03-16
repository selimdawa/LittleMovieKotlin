package com.flatcode.littlemovieadmin.Unit

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Modelimport.Category
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA.castList
import com.flatcode.littlemovieadmin.Unit.DATA.movieList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import jp.wasabeef.glide.transformations.BlurTransformation
import java.text.MessageFormat

object VOID {
    fun IntentClear(context: Context, c: Class<*>?) {
        val intent = Intent(context, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun Intent1(context: Context, c: Class<*>?) {
        val intent = Intent(context, c)
        context.startActivity(intent)
    }

    fun IntentExtra(context: Context, c: Class<*>?, key: String?, value: String?) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        context.startActivity(intent)
    }

    fun IntentExtra2(
        context: Context, c: Class<*>?, key: String?, value: String?,
        key2: String?, value2: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        context.startActivity(intent)
    }

    fun IntentExtra4(
        context: Context,
        c: Class<*>?, key: String?, value: String?, key2: String?, value2: String?,
        key3: String?, value3: String?, key4: String?, value4: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        intent.putExtra(key3, value3)
        intent.putExtra(key4, value4)
        context.startActivity(intent)
    }

    fun GlideImage(isUser: Boolean, context: Context?, Url: String?, Image: ImageView) {
        try {
            if (Url == DATA.BASIC) {
                if (isUser) Image.setImageResource(R.drawable.basic_user) else Image.setImageResource(
                    R.drawable.basic_music
                )
            } else Glide.with(context!!).load(Url).placeholder(R.color.image_profile).into(Image)
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun GlideBlur(isUser: Boolean, context: Context?, Url: String?, Image: ImageView, level: Int) {
        try {
            if (Url == DATA.BASIC) {
                if (isUser) Image.setImageResource(R.drawable.basic_user) else Image.setImageResource(
                    R.drawable.basic_music
                )
            } else Glide.with(context!!).load(Url).placeholder(R.color.image_profile)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(level))).into(Image)
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun GlideBlurUri(context: Context?, uri: Uri?, Image: ImageView?, level: Int) {
        if (uri != null) Glide.with(context!!).load(uri).placeholder(R.color.image_profile)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(level))).into(Image!!)
    }

    fun incrementItemCount(database: String?, id: String?, childDB: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var itemsCount = DATA.EMPTY + snapshot.child(childDB!!).value
                if (itemsCount == DATA.EMPTY || itemsCount == DATA.NULL) {
                    itemsCount = DATA.EMPTY + DATA.ZERO
                }
                val newItemsCount = itemsCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[childDB] = newItemsCount
                val reference = FirebaseDatabase.getInstance().getReference(database)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementItemRemoveCount(database: String?, id: String?, childDB: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATA.EMPTY + snapshot.child(childDB!!).value
                if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL)
                    lovesCount = DATA.EMPTY + DATA.ZERO

                val i = lovesCount.toInt()
                if (i > 0) {
                    val removeLovesCount = lovesCount.toInt() - 1
                    val hashMap = HashMap<String?, Any>()
                    hashMap[childDB] = removeLovesCount
                    val reference = FirebaseDatabase.getInstance().getReference(database)
                    reference.child(id).updateChildren(hashMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun isFavorite(add: ImageView, Id: String?, UserId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(
            UserId!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(Id!!).exists()) {
                    add.setImageResource(R.drawable.ic_star_selected)
                    add.tag = "added"
                } else {
                    add.setImageResource(R.drawable.ic_star_unselected)
                    add.tag = "add"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun checkFavorite(image: ImageView, id: String?) {
        if (image.tag == "add") FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid)
            .child(id!!).setValue(true) else FirebaseDatabase.getInstance()
            .getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
            .child(id!!).removeValue()
    }

    fun nrLoves(number: TextView, id: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(
            id!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                number.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun CropImageSquare(activity: Activity?) {
        CropImage.activity()
            .setMinCropResultSize(DATA.MIX_SQUARE, DATA.MIX_SQUARE)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun CropVideoSquare(activity: Activity?) {
        CropImage.activity()
            .setMinCropResultSize(DATA.MIX_VIDEO_X, DATA.MIX_VIDEO_Y)
            .setAspectRatio(10, 14)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun CropImageSlider(activity: Activity?) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setMinCropResultSize(DATA.MIX_SLIDER_X, DATA.MIX_SLIDER_Y)
            .setAspectRatio(16, 9)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun Intro(context: Context?, background: ImageView, backWhite: ImageView, backDark: ImageView) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context!!)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            background.setImageResource(R.drawable.background_day)
            backWhite.visibility = View.VISIBLE
            backDark.visibility = View.GONE
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            background.setImageResource(R.drawable.background_night)
            backWhite.visibility = View.GONE
            backDark.visibility = View.VISIBLE
        }
    }

    fun Logo(context: Context?, background: ImageView) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context!!)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            background.setImageResource(R.drawable.logo)
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            background.setImageResource(R.drawable.logo_night)
        }
    }

    fun getFileExtension(uri: Uri?, context: Context): String? {
        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    fun moreDeleteCategory(
        activity: Activity,
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
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra(activity, CLASS.CATEGORY_EDIT, DATA.CATEGORY_ID, id)
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity, id, name, DATA.CATEGORY, DATA.CATEGORIES,
                        false, DB, idDB, childDB, cast, movie,
                    )
                }
            }.show()
    }

    fun moreDeleteCast(
        activity: Activity, item: Cast?, DB: String?, idDB: String?, childDB: String?,
        cast: Boolean?, movie: Boolean?,
    ) {
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra(activity, CLASS.CAST_EDIT, DATA.CAST_ID, id)
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity, id, name, DATA.CAST, DATA.CAST,
                        false, DB, idDB, childDB, cast, movie,
                    )
                }
            }.show()
    }

    fun moreDeleteMovie(
        activity: Activity, item: Movie?, DB: String?, idDB: String?, childDB: String?,
        cast: Boolean?, movie: Boolean?,
    ) {
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val categoryId = DATA.EMPTY + item.categoryId
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra2(
                        activity, CLASS.MOVIE_EDIT, DATA.MOVIE_ID, id, DATA.CATEGORY_ID, categoryId
                    )
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity, id, name, DATA.MOVIE, DATA.MOVIES,
                        false, DB, idDB, childDB, cast, movie,
                    )
                }
            }.show()
    }

    fun dialogOptionDelete(
        activity: Activity, id: String?, name: String, type: String?, nameDB: String?,
        isEditorsChoice: Boolean, DB: String?, idDB: String?, childDB: String?,
        cast: Boolean?, movie: Boolean?,
    ) {
        val dialog = Dialog(activity)
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
            if (isEditorsChoice) dialogUpdateEditorsChoice(dialog, activity, id) else deleteDB(
                dialog, activity, id, name, nameDB, DB, idDB, childDB
            )
            if (cast!!) deleteCastInfo(id!!) else if (movie!!) deleteMovieInfo(id!!)
        }

        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun deleteMovieInfo(id: String) {
        castList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE).child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                castList!!.clear()
                for (snapshot in dataSnapshot.children)
                    castList!!.add(snapshot.key!!)

                var i = 0
                while (castList!!.size > i) {
                    incrementItemRemoveCount(DATA.CAST, castList!![i], DATA.MOVIES_COUNT)
                    i++
                }
                ref.removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun deleteCastInfo(id: String) {
        movieList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                movieList!!.clear()
                for (snapshot in dataSnapshot.children)
                    if (snapshot.child(id).exists()) movieList!!.add(snapshot.key!!)

                var i = 0
                while (movieList!!.size > i) {
                    ref.child(movieList!![i]).child(id).removeValue()
                    incrementItemRemoveCount(DATA.MOVIES, movieList!![i], DATA.CAST_COUNT)
                    i++
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun dialogUpdateEditorsChoice(dialogDelete: Dialog, context: Context?, id: String?) {
        val dialog = ProgressDialog(context)
        dialog.setMessage("Updating Editors Choice...")
        dialog.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.EDITORS_CHOICE] = 0

        val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
            dialogDelete.dismiss()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(context, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
            dialogDelete.dismiss()
        }
    }

    fun deleteDB(
        dialogDelete: Dialog, activity: Activity, id: String?, name: String, nameDB: String?,
        DB: String?, idDB: String?, childDB: String?,
    ) {
        val dialog = ProgressDialog(activity)
        dialog.setTitle("Please wait")
        dialog.setMessage("Deleting $name ...")
        dialog.show()
        val reference = FirebaseDatabase.getInstance().getReference(nameDB!!)
        reference.child(id!!).removeValue().addOnSuccessListener {
            if ((DB != null) and (idDB != null) and (childDB != null))
                incrementItemRemoveCount(DB, idDB, childDB)
            DATA.isChange = true
            activity.onBackPressed()
            dialog.dismiss()
            Toast.makeText(activity, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
            dialogDelete.dismiss()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(activity, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun addToEditorsChoice(context: Context?, activity: Activity, id: String?, number: Int) {
        val dialog = ProgressDialog(context)
        dialog.setMessage("Updating Editors Choice...")
        dialog.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.EDITORS_CHOICE] = number
        val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
            activity.finish()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(context, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun dialogAboutArtist(context: Context?, imageDB: String?, nameDB: String?, aboutDB: String?) {
        val dialog = Dialog(context!!)
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

        GlideImage(false, context, imageDB, image)
        name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
        aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun convertDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun loadCategory(categoryId: String?, category: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.child(categoryId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Category = DATA.EMPTY + snapshot.child(DATA.NAME).value
                category.text = Category
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}