package com.flatcode.littlemovie.utils

import android.R
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.core.net.toUri
import com.flatcode.littlemovie.databinding.DialogAboutAppBinding
import com.flatcode.littlemovie.databinding.DialogAboutArtistBinding
import com.flatcode.littlemovie.databinding.DialogCloseAppBinding
import com.flatcode.littlemovie.databinding.DialogLogoutBinding
import com.flatcode.littlemovie.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.MessageFormat

fun Context.closeApp() {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogCloseAppBinding.inflate(activity.layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

    dialogBinding.yes.setOnClickListener {
        activity.finish()
    }

    dialogBinding.no.setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Context.dialogLogout() {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogLogoutBinding.inflate(activity.layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

    dialogBinding.yes.setOnClickListener {
        FirebaseAuth.getInstance().signOut()
        openActivity<AuthActivity>(clear = true)
        alertDialog.dismiss()
    }

    dialogBinding.no.setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Context.dialogAboutApp() {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogAboutAppBinding.inflate(activity.layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

    dialogBinding.website.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW, DATA.WEB_SITE.toUri())
        startActivity(intent)
    }

    dialogBinding.facebook.setOnClickListener {
        val fbAppIntent = Intent(Intent.ACTION_VIEW, "fb://profile/${DATA.FB_ID}".toUri()).apply {
            setPackage("com.facebook.katana")
        }
        val fbWebIntent = Intent(Intent.ACTION_VIEW, "https://facebook.com/${DATA.FB_ID}".toUri())

        try {
            startActivity(fbAppIntent)
        } catch (_: ActivityNotFoundException) {
            startActivity(fbWebIntent)
        } catch (_: Exception) {
            startActivity(fbWebIntent)
        }
    }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogAboutArtistBinding.inflate(activity.layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

    dialogBinding.image.loadImage(false, imageDB)
    dialogBinding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
    dialogBinding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}