package com.flatcode.littlemovie.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.DialogAboutAppBinding
import com.flatcode.littlemovie.databinding.DialogAboutArtistBinding
import com.flatcode.littlemovie.databinding.DialogCloseAppBinding
import com.flatcode.littlemovie.databinding.DialogLogoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.MessageFormat

object VOID {
    fun IntentClear(context: Context?, c: Class<*>?) {
        val intent = Intent(context, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }

    fun Intent1(context: Context?, c: Class<*>?) {
        val intent = Intent(context, c)
        context!!.startActivity(intent)
    }

    fun IntentExtra(context: Context?, c: Class<*>?, key: String?, value: String?) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        context!!.startActivity(intent)
    }

    fun IntentExtra2(
        context: Context?, c: Class<*>?, key: String?, value: String?,
        key2: String?, value2: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        context!!.startActivity(intent)
    }

    fun IntentExtra3(
        context: Context?, c: Class<*>?, key: String?, value: String?,
        key2: String?, value2: String?, key3: String?, value3: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        intent.putExtra(key3, value3)
        context!!.startActivity(intent)
    }

    fun IntentExtra4(
        context: Context, c: Class<*>?, key: String?, value: String?, key2: String?,
        value2: String?, key3: String?, value3: String?, key4: String?, value4: String?,
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
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user)
                } else {
                    Image.setImageResource(R.drawable.basic_music)
                }
            } else {
                Image.load(Url) {
                    placeholder(R.color.image_profile)
                    crossfade(true)
                }
            }
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun GlideBlur(isUser: Boolean, context: Context?, Url: String?, Image: ImageView, level: Int) {
        try {
            if (Url == DATA.BASIC) {
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user)
                } else {
                    Image.setImageResource(R.drawable.basic_music)
                }
            } else {
                Image.load(Url) {
                    placeholder(R.color.image_profile)
                    transformations(SimpleBlurTransformation(level.toFloat()))
                }
            }
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun closeApp(context: Context?, a: Activity?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogCloseAppBinding.inflate(LayoutInflater.from(context))
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

    fun dialogLogout(context: Context?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        binding.yes.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            IntentClear(context, CLASS.AUTH)
        }
        binding.no.setOnClickListener { dialog.cancel() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun shareApp(context: Context?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            " Download the app now from Google Play " + " https://play.google.com/store/apps/details?id=" + context!!.packageName
        )
        context.startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
    }

    fun rateApp(context: Context?) {
        val uri = Uri.parse("market://details?id=" + context!!.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    fun dialogAboutApp(context: Context?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAboutAppBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        binding.website.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                context.startActivity(websiteIntent)
            }

            val websiteIntent: Intent get() = Intent(Intent.ACTION_VIEW, Uri.parse(DATA.WEB_SITE))
        })
        binding.facebook.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                context.startActivity(openFacebookIntent)
            }

            val openFacebookIntent: Intent
                get() = try {
                    context.packageManager.getPackageInfo("com.facebook.katana", 0)
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

    fun CropImageSquare(activity: Activity?) {
        CropImage.activity()
            .setMinCropResultSize(DATA.MIX_SQUARE, DATA.MIX_SQUARE)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun getFileExtension(uri: Uri?, context: Context): String {
        val cR: ContentResolver = context.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))!!
    }

    fun dialogAboutArtist(context: Context?, imageDB: String?, nameDB: String?, aboutDB: String?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAboutArtistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        GlideImage(false, context, imageDB, binding.image)
        binding.name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB))
        binding.aboutTheArtist.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun isInterested(add: ImageView, id: String?, type: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
        ref.child(DATA.FirebaseUserUid).child(type!!).child(id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        add.setImageResource(R.drawable.ic_star_selected)
                        add.tag = "added"
                    } else {
                        add.setImageResource(R.drawable.ic_star_unselected)
                        add.tag = "add"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun checkInterested(add: ImageView, type: String?, id: String?) {
        if (add.tag == "add") {
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

    fun isFavorite(add: ImageView, id: String?, userId: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
        ref.child(userId!!).child(id!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    add.setImageResource(R.drawable.ic_heart_selected)
                    add.tag = "added"
                } else {
                    add.setImageResource(R.drawable.ic_heart_unselected)
                    add.tag = "add"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkFavorite(add: ImageView, id: String?) {
        if (add.tag == "add") {
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                .child(DATA.FirebaseUserUid).child(id!!).setValue(true)
        } else {
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                .child(DATA.FirebaseUserUid).child(id!!).removeValue()
        }
    }

    fun isLoves(add: ImageView, id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
        ref.child(DATA.FirebaseUserUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    add.setImageResource(R.drawable.ic_heart_selected)
                    add.tag = "added"
                } else {
                    add.setImageResource(R.drawable.ic_heart_unselected)
                    add.tag = "add"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun nrLoves(number: TextView, id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                number.text = MessageFormat.format("{0}", snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkLove(add: ImageView, id: String?) {
        if (add.tag == "add") {
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

    fun incrementViewCount(id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(id!!)
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

    fun convertDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
