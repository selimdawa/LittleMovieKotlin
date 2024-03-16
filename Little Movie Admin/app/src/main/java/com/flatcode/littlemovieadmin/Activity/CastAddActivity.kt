package com.flatcode.littlemovieadmin.Activityimport

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityCastAddBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class CastAddActivity : AppCompatActivity() {

    private var binding: ActivityCastAddBinding? = null
    var activity: Activity = this@CastAddActivity
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCastAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.toolbar.nameSpace.setText(R.string.add_new_cast)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(activity) }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var aboutMy = DATA.EMPTY
    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        aboutMy = binding!!.aboutMyEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(aboutMy)) {
            Toast.makeText(activity, "Enter About My...", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(activity, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else {
            uploadToStorage()
        }
    }

    private fun uploadToStorage() {
        dialog!!.setMessage("Uploading Cast...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST)
        val id = ref.push().key
        val filePathAndName = "Images/Cast/$id"
        val reference = FirebaseStorage.getInstance()
            .getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity))
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                uploadInfoDB(uploadedImageUrl, id, ref)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Cast upload failed due to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadInfoDB(uploadedImageUrl: String, id: String?, ref: DatabaseReference) {
        dialog!!.setMessage("Uploading Cast info...")
        dialog!!.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.ABOUT_MY] = DATA.EMPTY + aboutMy
        hashMap[DATA.IMAGE] = uploadedImageUrl
        hashMap[DATA.INTERESTED_COUNT] = DATA.ZERO
        hashMap[DATA.MOVIES_COUNT] = DATA.ZERO
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failure to upload to db due to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(activity, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(activity, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSquare(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}