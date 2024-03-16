package com.flatcode.littlemovieadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.DATA.castMovie
import com.flatcode.littlemovieadmin.Unit.DATA.castMovieOld
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unit.VOID.incrementItemCount
import com.flatcode.littlemovieadmin.Unit.VOID.incrementItemRemoveCount
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityMovieEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.text.MessageFormat

class MovieEditActivity : AppCompatActivity() {

    private var binding: ActivityMovieEditBinding? = null
    var activity: Activity = this@MovieEditActivity
    private var movieId: String? = null
    private var category: String? = null
    private var categoryList: ArrayList<String>? = null
    private var categoryId: ArrayList<String>? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        movieId = intent.getStringExtra(DATA.MOVIE_ID)
        category = intent.getStringExtra(DATA.CATEGORY_ID)

        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        loadCategories()
        loadInfo()

        binding!!.toolbar.nameSpace.setText(R.string.edit_movie)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.editImage.setOnClickListener { VOID.CropVideoSquare(activity) }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var description = DATA.EMPTY
    private var yearText = DATA.EMPTY
    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null

    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        description = binding!!.descriptionEt.text.toString().trim { it <= ' ' }
        yearText = binding!!.yearEt.text.toString()

        //validate data
        if (TextUtils.isEmpty(name)) Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT)
            .show() else if (TextUtils.isEmpty(description)) Toast.makeText(
            activity, "Enter Description...", Toast.LENGTH_SHORT
        ).show() else if (TextUtils.isEmpty(yearText)) Toast.makeText(
            activity, "Enter Date...", Toast.LENGTH_SHORT
        ).show() else if (yearText.toInt() < DATA.MIN_YEAR || yearText.toInt() > DATA.MAX_YEAR)
            Toast.makeText(
                activity, "Invalid Date...", Toast.LENGTH_SHORT
            ).show() else if (category!!.isEmpty()) Toast.makeText(
            activity, "Pick Category...", Toast.LENGTH_SHORT
        ).show() else if (castMovie.size <= 0) Toast.makeText(
            activity, "Enter Cast...", Toast.LENGTH_SHORT
        ).show() else {
            if (imageUri == null) {
                update(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun update(imageUrl: String) {
        dialog!!.setMessage("Updating Movie DB...")
        dialog!!.show()

        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.DESCRIPTION] = DATA.EMPTY + description
        hashMap[DATA.YEAR] = yearText.toInt()
        hashMap[DATA.CAST_COUNT] = castMovie.size
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        if (imageUri != null) hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl

        val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        reference.child(movieId!!).updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Movie updated...", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (selectedCategoryId != category) {
                incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT)
                if (category != null) incrementItemRemoveCount(
                    DATA.CATEGORIES, category, DATA.MOVIES_COUNT
                )
            }
            updateCast()
            dialog!!.dismiss()
            onBackPressed()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity,
                "Failed to update db duo to : " + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadImage() {
        dialog!!.setMessage("Updating Movie Image...")
        dialog!!.show()

        val filePathAndName = "Images/Movie/$movieId"
        val reference = FirebaseStorage.getInstance().getReference(
            filePathAndName
                    + DATA.DOT + VOID.getFileExtension(imageUri, activity)
        )
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result

                update(uploadedImageUrl)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to upload image due to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateCast() {
        dialog!!.setMessage("Updating Cast Movie...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)

        //Remove from moviesCount + CastCount
        for (r in castMovieOld.indices) {
            if (!castMovie.contains(castMovieOld[r])) {
                ref.child(movieId!!).child(castMovieOld[r]!!).removeValue()
                incrementItemRemoveCount(DATA.CAST, castMovieOld[r], DATA.MOVIES_COUNT)
            }
        }

        //Add to moviesCount
        for (t in castMovie.indices) if (!castMovieOld.contains(castMovie[t]))
            incrementItemCount(DATA.CAST, castMovie[t], DATA.MOVIES_COUNT)

        //Add to CastCount
        val hashMap = HashMap<String?, Any>()
        for (i in castMovie.indices)
            hashMap[castMovie[i]] = true

        assert(movieId != null)
        ref.child(movieId!!).setValue(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Cast Movie updated...", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (selectedCategoryId != category) {
                incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT)
                if (category != null) incrementItemRemoveCount(
                    DATA.CATEGORIES, category, DATA.MOVIES_COUNT
                )
            }
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failed to update db duo to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        reference.child(movieId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Movie::class.java)!!

                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).value
                val name = DATA.EMPTY + item.name
                val image = DATA.EMPTY + item.image
                val description = DATA.EMPTY + item.description
                val year = DATA.EMPTY + item.year
                val durations = DATA.EMPTY + item.duration
                val castCount = DATA.EMPTY + item.castCount

                VOID.GlideImage(true, activity, image, binding!!.image)
                VOID.GlideBlur(false, activity, image, binding!!.imageBlur, 50)
                binding!!.nameEt.setText(name)
                binding!!.descriptionEt.setText(description)
                binding!!.yearEt.setText(year)
                binding!!.duration.text = VOID.convertDuration(durations.toLong())
                binding!!.cast.text = castCount

                val refCategory = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
                refCategory.child(selectedCategoryId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //get category
                            val category = DATA.EMPTY + snapshot.child(DATA.NAME).value
                            binding!!.category.text = category
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                val refCast = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
                refCast.child(movieId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            castMovie.clear()
                            for (snapshot in dataSnapshot.children) {
                                castMovie.add(snapshot.key)
                                castMovieOld.add(snapshot.key)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadCategories() {
        categoryList = ArrayList()
        categoryId = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList!!.clear()
                categoryId!!.clear()
                for (data in snapshot.children) {
                    val id = DATA.EMPTY + data.child(DATA.ID).value
                    val name = DATA.EMPTY + data.child(DATA.NAME).value

                    categoryList!!.add(name)
                    categoryId!!.add(id)
                }
                binding!!.cast.setOnClickListener { VOID.Intent1(activity, CLASS.CAST_MOVIE) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun categoryPickDialog() {
        val categories = arrayOfNulls<String>(categoryList!!.size)
        for (i in categoryList!!.indices)
            categories[i] = categoryList!![i]
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pick Category")
            .setItems(categories) { dialog: DialogInterface?, which: Int ->
                selectedCategoryTitle = categoryList!![which]
                selectedCategoryId = categoryId!![which]
                binding!!.category.text = selectedCategoryTitle
            }.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(activity, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(activity, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropVideoSquare(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
                VOID.GlideBlurUri(activity, imageUri, binding!!.imageBlur, 50)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.cast.text = MessageFormat.format("{0}{1}", DATA.EMPTY, castMovie.size)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        castMovie.clear()
        castMovieOld.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        castMovie.clear()
        castMovieOld.clear()
    }
}