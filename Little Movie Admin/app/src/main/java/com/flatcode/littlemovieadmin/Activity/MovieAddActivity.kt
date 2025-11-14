package com.flatcode.littlemovieadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.DATA.castMovie
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unit.VOID.incrementItemCount
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityMovieAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.text.MessageFormat

class MovieAddActivity : AppCompatActivity() {

    private var binding: ActivityMovieAddBinding? = null
    var activity: Activity = this@MovieAddActivity

    var imageUri: Uri? = null
    var videoUri: Uri? = null

    private var uploadsTask: StorageTask<*>? = null
    private var dialog: ProgressDialog? = null

    private var categoryId: ArrayList<String>? = null
    private var categoryList: ArrayList<String>? = null

    var durations: String? = null
    var metadataRetriever: MediaMetadataRetriever? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityMovieAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        loadCategories()

        binding!!.toolbar.nameSpace.setText(R.string.add_new_movie)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.image.setOnClickListener { VOID.CropVideoSquare(activity) }
        binding!!.chooseMovie.setOnClickListener { openVideoFiles() }
        binding!!.toolbar.ok.setOnClickListener { validateData() }

        metadataRetriever = MediaMetadataRetriever()
    }

    private var name = DATA.EMPTY
    private var description = DATA.EMPTY
    private var yearText = DATA.EMPTY

    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        description = binding!!.descriptionEt.text.toString().trim { it <= ' ' }
        yearText = binding!!.yearEt.text.toString()

        //validate data
        if (TextUtils.isEmpty(name)) Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT)
            .show() else if (TextUtils.isEmpty(description)) Toast.makeText(
            activity,
            "Enter Description...", Toast.LENGTH_SHORT
        ).show() else if (TextUtils.isEmpty(yearText)) Toast.makeText(
            activity,
            "Enter Date...", Toast.LENGTH_SHORT
        ).show() else if (yearText.toInt() < DATA.MIN_YEAR || yearText.toInt() > DATA.MAX_YEAR) Toast.makeText(
            activity, "Invalid Date...", Toast.LENGTH_SHORT
        ).show() else if (TextUtils.isEmpty(selectedCategoryTitle)) Toast.makeText(
            activity,
            "Pick Category...",
            Toast.LENGTH_SHORT
        ).show() else if (castMovie.size <= 0) Toast.makeText(
            activity,
            "Enter Cast...",
            Toast.LENGTH_SHORT
        ).show() else if (imageUri == null) Toast.makeText(
            activity,
            "Pick Image...",
            Toast.LENGTH_SHORT
        ).show() else if (videoUri == null) Toast.makeText(
            activity,
            "Pick Movie...",
            Toast.LENGTH_SHORT
        ).show() else uploadFileToDB()
    }

    fun uploadFileToDB() {
        val message = binding!!.choose.text.toString()
        if (message == "No file Selected") {
            Toast.makeText(this, "Please selected an image!", Toast.LENGTH_SHORT).show()
        } else {
            if (uploadsTask != null && uploadsTask!!.isInProgress) {
                Toast.makeText(this, "Movies uploads in already progress!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadVideoToStorage()
            }
        }
    }

    private fun uploadVideoToStorage() {
        Toast.makeText(this, "Uploads please wait!", Toast.LENGTH_SHORT).show()
        dialog!!.setMessage("Uploads Movie...")
        dialog!!.show()

        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        val id = ref.push().key
        val filePathAndName = "Movies/$id"
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedVideoUrl = "" + uriTask.result
                dialog!!.dismiss()
                uploadToStorage(uploadedVideoUrl, id, ref)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(this, "Error ! " + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                dialog!!.setMessage("uploaded " + progress.toInt() + "%.....")
            }
    }

    private fun uploadToStorage(uploadedMovieUrl: String, id: String?, ref: DatabaseReference) {
        dialog!!.setMessage("Uploading Movie...")
        dialog!!.show()
        val filePathAndName = "Images/Movie/$id"
        val reference = FirebaseStorage.getInstance()
            .getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity))
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                uploadInfoToDB(uploadedMovieUrl, uploadedImageUrl, id, ref)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity,
                    "Cast upload failed due to : " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                dialog!!.setMessage("uploaded " + progress.toInt() + "%.....")
            }
    }

    private fun uploadInfoToDB(
        uploadedMovieUrl: String, uploadedImageUrl: String, id: String?, ref: DatabaseReference,
    ) {
        dialog!!.setMessage("Uploading movie info...")
        dialog!!.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.DESCRIPTION] = DATA.EMPTY + description
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        hashMap[DATA.DURATION] = DATA.EMPTY + durations
        hashMap[DATA.YEAR] = yearText.toInt()
        hashMap[DATA.MOVIE_LINK] = DATA.EMPTY + uploadedMovieUrl
        hashMap[DATA.IMAGE] = DATA.EMPTY + uploadedImageUrl
        hashMap[DATA.EDITORS_CHOICE] = DATA.ZERO
        hashMap[DATA.LOVES_COUNT] = DATA.ZERO
        hashMap[DATA.VIEWS_COUNT] = DATA.ZERO
        hashMap[DATA.CAST_COUNT] = castMovie.size

        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            if (selectedCategoryId != null) incrementItemCount(
                DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT
            )
            uploadCastToDB(id)
            dialog!!.dismiss()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failure to upload to db due to :" + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadCastToDB(id: String?) {
        dialog!!.setMessage("Uploading Cast Movie...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)

        //setup data to upload
        val hashMap = HashMap<String?, Any>()
        for (i in castMovie.indices) {
            hashMap[castMovie[i]] = true
            incrementItemCount(DATA.CAST, castMovie[i], DATA.MOVIES_COUNT)
        }

        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failure to upload to db due to :" + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openVideoFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data!!.data != null) {
            videoUri = data.data
            //duration
            metadataRetriever!!.setDataSource(this, videoUri)
            assert(metadataRetriever != null)
            durations =
                metadataRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            binding!!.duration.text = VOID.convertDuration(durations!!.toLong())
            //Ok Choose Movie
            binding!!.choose.setText(R.string.ok)
        }
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
                assert(result != null)
                imageUri = result!!.uri
                binding!!.image.setImageURI(imageUri)
                VOID.GlideBlurUri(activity, imageUri, binding!!.imageBlur, 50)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
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

    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
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

    override fun onResume() {
        super.onResume()
        binding!!.cast.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, castMovie.size)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        castMovie.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        castMovie.clear()
    }
}