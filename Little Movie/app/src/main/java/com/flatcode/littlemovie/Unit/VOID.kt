package com.flatcode.littlemovie.Unit

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flatcode.littlemovie.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import jp.wasabeef.glide.transformations.BlurTransformation
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
                Glide.with(context!!).load(Url).placeholder(R.color.image_profile).into(Image)
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
                Glide.with(context!!).load(Url).placeholder(R.color.image_profile)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(level))).into(Image)
            }
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun closeApp(context: Context?, a: Activity?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_close_app)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.findViewById<View>(R.id.yes).setOnClickListener { a!!.finish() }
        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.cancel() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun dialogLogout(context: Context?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.findViewById<View>(R.id.yes).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            IntentClear(context, CLASS.AUTH)
        }
        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.cancel() }
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
        dialog.setContentView(R.layout.dialog_about_app)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.findViewById<View>(R.id.website).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                context.startActivity(websiteIntent)
            }

            val websiteIntent: Intent get() = Intent(Intent.ACTION_VIEW, Uri.parse(DATA.WEB_SITE))
        })
        dialog.findViewById<View>(R.id.facebook).setOnClickListener(object : View.OnClickListener {
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

    fun Intro(context: Context?, background: ImageView, backWhite: ImageView, backDark: ImageView) {
        val sharedPreferences: SharedPreferences = PreferenceManager
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
        val sharedPreferences: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context!!)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            background.setImageResource(R.drawable.logo)
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            background.setImageResource(R.drawable.logo_night)
        }
    }

    fun incrementViewCount(id: String?) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).getValue()
                if (viewsCount == DATA.EMPTY || viewsCount == DATA.NULL) {
                    viewsCount = "0"
                }
                val newViewsCount = viewsCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATA.VIEWS_COUNT] = newViewsCount
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementLovesCount(id: String?) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
                if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                    lovesCount = "0"
                }
                val newLovesCount = lovesCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATA.LOVES_COUNT] = newLovesCount
                hashMap[DATA.LOVES_COUNT] = newLovesCount
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementLovesRemoveCount(id: String?) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATA.EMPTY + snapshot.child("lovesCount").value
                if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                    lovesCount = "0"
                }
                val removeLovesCount = lovesCount.toLong() - 1
                val hashMap = HashMap<String, Any>()
                hashMap["lovesCount"] = removeLovesCount
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementItemCount(database: String?, id: String?, childDB: String) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var itemsCount = DATA.EMPTY + snapshot.child(childDB).value
                if (itemsCount == DATA.EMPTY || itemsCount == DATA.NULL) {
                    itemsCount = DATA.EMPTY + DATA.ZERO
                }
                val newItemsCount = itemsCount.toLong() + 1
                val hashMap = HashMap<String, Any>()
                hashMap[childDB] = newItemsCount
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference(database)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementItemRemoveCount(database: String?, id: String?, childDB: String) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATA.EMPTY + snapshot.child(childDB).value
                if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                    lovesCount = DATA.EMPTY + DATA.ZERO
                }
                val removeLovesCount = lovesCount.toLong() - 1
                val hashMap = HashMap<String, Any>()
                hashMap[childDB] = removeLovesCount
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference(database)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun isFavorite(add: ImageView, Id: String?, UserId: String?) {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child(DATA.FAVORITES).child(UserId!!)
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

    fun checkLove(image: ImageView, id: String?) {
        if (image.tag == "love") {
            FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
                .child(DATA.FirebaseUserUid).setValue(true)
            incrementLovesCount(id)
        } else {
            FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
                .child(DATA.FirebaseUserUid).removeValue()
            incrementLovesRemoveCount(id)
        }
    }

    fun isLoves(image: ImageView, id: String?) {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child(DATA.LOVES).child(id!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                    image.setImageResource(R.drawable.ic_heart_selected)
                    image.tag = "loved"
                } else {
                    image.setImageResource(R.drawable.ic_heart_unselected)
                    image.tag = "love"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun nrLoves(number: TextView, id: String?) {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child(DATA.LOVES).child(id!!)
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

    fun getFileExtension(uri: Uri?, context: Context): String {
        val cR: ContentResolver = context.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))!!
    }

    fun dialogAboutArtist(context: Context?, imageDB: String?, nameDB: String?, aboutDB: String?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_about_artist)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val image = dialog.findViewById<ImageView>(R.id.image)
        val name: TextView = dialog.findViewById(R.id.name)
        val aboutTheArtist: TextView = dialog.findViewById(R.id.aboutTheArtist)
        GlideImage(false, context, imageDB, image)
        name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB))
        aboutTheArtist.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun convertDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun incrementInterestedCount(id: String?, type: String?) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var interestedCount =
                    DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).getValue()
                if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                    interestedCount = "0"
                }
                val newInterestedCount = interestedCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATA.INTERESTED_COUNT] = newInterestedCount
                val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(type)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementInterestedRemoveCount(id: String?, type: String?) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var interestedCount =
                    DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).getValue()
                if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                    interestedCount = "0"
                }
                val removeInterestedCount = interestedCount.toLong() - 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATA.INTERESTED_COUNT] = removeInterestedCount
                val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(type)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun isInterested(add: ImageView, Id: String?, type: String?) {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid).child(type!!)
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

    fun checkInterested(image: ImageView, type: String?, id: String?) {
        if (image.tag == "add") {
            incrementInterestedCount(id, type)
            FirebaseDatabase.getInstance().getReference().child(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid)
                .child(type!!).child(id!!).setValue(true)
        } else {
            incrementInterestedRemoveCount(id, type)
            FirebaseDatabase.getInstance().getReference().child(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid)
                .child(type!!).child(id!!).removeValue()
        }
    }

    fun loadCategory(categoryId: String?, category: TextView) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.child(categoryId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Category = DATA.EMPTY + snapshot.child(DATA.NAME).value
                category.text = Category
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}