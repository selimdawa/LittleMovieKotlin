package com.flatcode.littlemovieadmin.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.DialogAboutArtistBinding
import com.flatcode.littlemovieadmin.databinding.DialogLogoutBinding
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.ui.cast.CastEditActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryEditActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieEditActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.text.MessageFormat

fun Context.createProgressDialog(message: String, title: String? = null): AlertDialog {
    val linearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(50, 50, 50, 50)
        gravity = Gravity.CENTER_VERTICAL
    }
    val progressBar = ProgressBar(this).apply {
        isIndeterminate = true
        setPadding(0, 0, 30, 0)
    }
    val textView = TextView(this).apply {
        text = message
        textSize = 16f
    }
    linearLayout.addView(progressBar)
    linearLayout.addView(textView)

    return AlertDialog.Builder(this).apply {
        if (title != null) setTitle(title)
    }.setView(linearLayout).setCancelable(false).create()
}

fun Activity.moreDeleteCategory(
    item: Category?,
    db: String?,
    idDB: String?,
    childDB: String?,
    cast: Boolean?,
    movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { _: DialogInterface?, which: Int ->
        if (which == 0) {
            this.openActivity<CategoryEditActivity>(extras = arrayOf(DATA.CATEGORY_ID to id))
        } else if (which == 1) {
            this.dialogOptionDelete(
                id, name, DATA.CATEGORY, DATA.CATEGORIES,
                isEditorsChoice = false, db, idDB, childDB, cast, movie,
            )
        }
    }.show()
}

fun Activity.moreDeleteCast(
    item: Cast?, db: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { _: DialogInterface?, which: Int ->
        if (which == 0) {
            this.openActivity<CastEditActivity>(extras = arrayOf(DATA.CAST_ID to id))
        } else if (which == 1) {
            this.dialogOptionDelete(
                id, name, DATA.CAST, DATA.CAST,
                isEditorsChoice = false, db, idDB, childDB, cast, movie,
            )
        }
    }.show()
}

fun Activity.moreDeleteMovie(
    item: Movie?, db: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    val id = DATA.EMPTY + item!!.id
    val name = DATA.EMPTY + item.name
    val categoryId = DATA.EMPTY + item.categoryId
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Choose Options").setItems(options) { _: DialogInterface?, which: Int ->
        if (which == 0) {
            this.openActivity<MovieEditActivity>(
                extras = arrayOf(
                    DATA.MOVIE_ID to id, DATA.CATEGORY_ID to categoryId
                )
            )
        } else if (which == 1) {
            this.dialogOptionDelete(
                id, name, DATA.MOVIE, DATA.MOVIES,
                isEditorsChoice = false, db, idDB, childDB, cast, movie,
            )
        }
    }.show()
}

fun Activity.dialogOptionDelete(
    id: String?, name: String, type: String?, nameDB: String?,
    isEditorsChoice: Boolean, db: String?, idDB: String?, childDB: String?,
    cast: Boolean?, movie: Boolean?,
) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.title.text = getString(R.string.delete_item_prompt, name, type)

    dialogBinding.yes.setOnClickListener {
        if (isEditorsChoice) this.dialogUpdateEditorsChoice(alertDialog, id) else this.deleteDB(
            alertDialog, id, name, nameDB, db, idDB, childDB
        )
        if (cast == true) deleteCastInfo(id!!) else if (movie == true) deleteMovieInfo(id!!)
    }

    dialogBinding.no.setOnClickListener { alertDialog.dismiss() }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Context.dialogUpdateEditorsChoice(dialogDelete: Dialog, id: String?) {
    val dialog = createProgressDialog("Updating Editors Choice...")
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
    db: String?, idDB: String?, childDB: String?,
) {
    val dialog = createProgressDialog("Deleting $name ...", "Please wait")
    dialog.show()
    val reference = FirebaseDatabase.getInstance().getReference(nameDB!!)
    reference.child(id!!).removeValue().addOnSuccessListener {
        if ((db != null) && (idDB != null) && (childDB != null)) incrementItemRemoveCount(
            db, idDB, childDB
        )
        DATA.isChange = true
        (this as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
            ?: @Suppress("DEPRECATION") this.onBackPressed()
        dialog.dismiss()
        Toast.makeText(this, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogAboutArtistBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.image.loadImage(imageDB, isUser = false)
    dialogBinding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
    dialogBinding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}