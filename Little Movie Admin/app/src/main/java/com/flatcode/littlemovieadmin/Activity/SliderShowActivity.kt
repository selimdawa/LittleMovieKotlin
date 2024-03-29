package com.flatcode.littlemovieadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivitySliderShowBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.text.MessageFormat

class SliderShowActivity : AppCompatActivity() {

    private var binding: ActivitySliderShowBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var IMAGE_NUMBER = 0
    private var item = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.slider_show)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.addOne.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 1
        }
        binding!!.addTwo.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 2
        }
        binding!!.addThree.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 3
        }
        binding!!.addFour.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 4
        }
        binding!!.addFive.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 5
        }
        binding!!.addSix.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 6
        }
        binding!!.addSeven.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 7
        }
        binding!!.addEight.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 8
        }
        binding!!.addNine.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 9
        }
        binding!!.addTeen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 10
        }
        binding!!.addEleven.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 11
        }
        binding!!.addTwelfth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 12
        }
        binding!!.addThirteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 13
        }
        binding!!.addFourteenth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 14
        }
        binding!!.addFifteenth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 15
        }
        binding!!.addSixteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 16
        }
        binding!!.addSeventeen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 17
        }
        binding!!.addEighteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 18
        }
        binding!!.addNineteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 19
        }
        binding!!.addTwenty.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 20
        }
        nrSliderShow
        sliderShow()
    }

    private val nrSliderShow: Unit
        get() {
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    item = dataSnapshot.childrenCount.toInt()
                    binding!!.toolbar.nameSpace.text =
                        MessageFormat.format("Slider Show ( {0} )", item)
                    if (item >= 0) {
                        binding!!.linearOne.visibility = View.VISIBLE
                    } else {
                        binding!!.linearOne.visibility = View.GONE
                    }
                    if (item >= 1) {
                        binding!!.linearTwo.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwo.visibility = View.GONE
                    }
                    if (item >= 2) {
                        binding!!.linearThree.visibility = View.VISIBLE
                    } else {
                        binding!!.linearThree.visibility = View.GONE
                    }
                    if (item >= 3) {
                        binding!!.linearFour.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFour.visibility = View.GONE
                    }
                    if (item >= 4) {
                        binding!!.linearFive.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFive.visibility = View.GONE
                    }
                    if (item >= 5) {
                        binding!!.linearSix.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSix.visibility = View.GONE
                    }
                    if (item >= 6) {
                        binding!!.linearSeven.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSeven.visibility = View.GONE
                    }
                    if (item >= 7) {
                        binding!!.linearEight.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEight.visibility = View.GONE
                    }
                    if (item >= 8) {
                        binding!!.linearNine.visibility = View.VISIBLE
                    } else {
                        binding!!.linearNine.visibility = View.GONE
                    }
                    if (item >= 9) {
                        binding!!.linearTeen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTeen.visibility = View.GONE
                    }
                    if (item >= 10) {
                        binding!!.linearEleven.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEleven.visibility = View.GONE
                    }
                    if (item >= 11) {
                        binding!!.linearTwelfth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwelfth.visibility = View.GONE
                    }
                    if (item >= 12) {
                        binding!!.linearThirteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearThirteen.visibility = View.GONE
                    }
                    if (item >= 13) {
                        binding!!.linearFourteenth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFourteenth.visibility = View.GONE
                    }
                    if (item >= 14) {
                        binding!!.linearFifteenth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFifteenth.visibility = View.GONE
                    }
                    if (item >= 15) {
                        binding!!.linearSixteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSixteen.visibility = View.GONE
                    }
                    if (item >= 16) {
                        binding!!.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 17) {
                        binding!!.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 18) {
                        binding!!.linearNineteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearNineteen.visibility = View.GONE
                    }
                    if (item >= 19) {
                        binding!!.linearTwenty.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwenty.visibility = View.GONE
                    }
                    binding!!.bar.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun sliderShow() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val one = DATA.EMPTY + dataSnapshot.child("1").value
                val two = DATA.EMPTY + dataSnapshot.child("2").value
                val three = DATA.EMPTY + dataSnapshot.child("3").value
                val four = DATA.EMPTY + dataSnapshot.child("4").value
                val five = DATA.EMPTY + dataSnapshot.child("5").value
                val six = DATA.EMPTY + dataSnapshot.child("6").value
                val seven = DATA.EMPTY + dataSnapshot.child("7").value
                val eight = DATA.EMPTY + dataSnapshot.child("8").value
                val nine = DATA.EMPTY + dataSnapshot.child("9").value
                val teen = DATA.EMPTY + dataSnapshot.child("10").value
                val eleven = DATA.EMPTY + dataSnapshot.child("11").value
                val twelfth = DATA.EMPTY + dataSnapshot.child("12").value
                val thirteen = DATA.EMPTY + dataSnapshot.child("13").value
                val fourteenth = DATA.EMPTY + dataSnapshot.child("14").value
                val fifteenth = DATA.EMPTY + dataSnapshot.child("15").value
                val sixteen = DATA.EMPTY + dataSnapshot.child("16").value
                val seventeen = DATA.EMPTY + dataSnapshot.child("17").value
                val eighteen = DATA.EMPTY + dataSnapshot.child("18").value
                val nineteen = DATA.EMPTY + dataSnapshot.child("19").value
                val twenty = DATA.EMPTY + dataSnapshot.child("20").value

                VOID.GlideImage(false, context, one, binding!!.imageOne)
                VOID.GlideImage(false, context, two, binding!!.imageTwo)
                VOID.GlideImage(false, context, three, binding!!.imageThree)
                VOID.GlideImage(false, context, four, binding!!.imageFour)
                VOID.GlideImage(false, context, five, binding!!.imageFive)
                VOID.GlideImage(false, context, six, binding!!.imageSix)
                VOID.GlideImage(false, context, seven, binding!!.imageSeven)
                VOID.GlideImage(false, context, eight, binding!!.imageEight)
                VOID.GlideImage(false, context, nine, binding!!.imageNine)
                VOID.GlideImage(false, context, teen, binding!!.imageTeen)
                VOID.GlideImage(false, context, eleven, binding!!.imageEleven)
                VOID.GlideImage(false, context, twelfth, binding!!.imageTwelfth)
                VOID.GlideImage(false, context, thirteen, binding!!.imageThirteen)
                VOID.GlideImage(false, context, fourteenth, binding!!.imageFourteenth)
                VOID.GlideImage(false, context, fifteenth, binding!!.imageFifteenth)
                VOID.GlideImage(false, context, sixteen, binding!!.imageSixteen)
                VOID.GlideImage(false, context, seventeen, binding!!.imageSeventeen)
                VOID.GlideImage(false, context, eighteen, binding!!.imageEighteen)
                VOID.GlideImage(false, context, nineteen, binding!!.imageNineteen)
                VOID.GlideImage(false, context, twenty, binding!!.imageTwenty)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun uploadImage(name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val filePathAndName = "Images/SliderShow/" + (DATA.EMPTY + name)
        val reference = FirebaseStorage.getInstance()
            .getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, context))
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                updateImage(uploadedImageUrl, DATA.EMPTY + name)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImage(imageUrl: String, name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val hashMap = HashMap<String, Any>()
        if (imageUri != null) {
            hashMap[DATA.EMPTY + name] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
        reference.updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(context, "The photo has been posted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSlider(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                uploadImage(DATA.EMPTY + IMAGE_NUMBER)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRestart() {
        nrSliderShow
        sliderShow()
        super.onRestart()
    }

    override fun onResume() {
        nrSliderShow
        sliderShow()
        super.onResume()
    }
}