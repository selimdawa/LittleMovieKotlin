package com.flatcode.littlemovieadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Modelimport.Category
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityCategoryAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class CategoryEditActivity : AppCompatActivity() {

    private var binding: ActivityCategoryAddBinding? = null

    var activity: Activity = this@CategoryEditActivity
    var categoryId: String? = null

    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        loadCategoryInfo()

        binding!!.toolbar.nameSpace.setText(R.string.edit_category)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(activity) }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private fun validateData() {
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter name...", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                updateCategory(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        dialog!!.setMessage("Updating Category Image...")
        dialog!!.show()
        val filePathAndName = "Images/Category/$categoryId"
        val reference = FirebaseStorage.getInstance().getReference(
            filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity)
        )
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                updateCategory(uploadedImageUrl)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to upload image due to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateCategory(imageUrl: String?) {
        dialog!!.setMessage("Updating Category DB...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        if (imageUri != null)
            hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl
        val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        reference.child(categoryId!!).updateChildren(hashMap)
            .addOnSuccessListener {
                dialog!!.dismiss()
                Toast.makeText(activity, "Category updated...", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to update db duo to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadCategoryInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        reference.child(categoryId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Category::class.java)!!
                val name = item.name
                val image = item.image

                VOID.GlideImage(true, activity, image, binding!!.image)
                binding!!.nameEt.setText(name)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
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
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}