package com.embed.pashudhan.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.embed.pashudhan.Firebase
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R


class PashuSalesActivity : AppCompatActivity() {

    companion object {
        private val TAG = "PashusalesActivity==>"
        private val PICK_IMAGE = 100
    }

    private val PashudhanDB = Firebase()
    private lateinit var mSelectedImage1: ImageView
    private lateinit var mSelectedImage2: ImageView
    private lateinit var mSelectedImage3: ImageView
    private lateinit var mSelectedImage4: ImageView
    private var mImageURI: Uri? = null
    private lateinit var mUserUUID: String
    private lateinit var mAnimalType: String
    private lateinit var mAnimalBreed: String
    private lateinit var mAnimalAge: String
    private lateinit var mAnimalByaat: String
    private lateinit var mAnimalMilkQuantity: String
    private lateinit var mAnimalMilkCapacity: String
    private lateinit var mAnimalPrice: String
    private lateinit var rootLayout: LinearLayout

    // Get Access to Helper Functions
    private var helper: Helper = Helper()

    var mImageList = ArrayList<Uri>()
    var mSelectedImageOrder = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashusales_activity_layout)

        rootLayout = findViewById(R.id.pashuSales_root_layout)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        mUserUUID = sharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        if (mUserUUID == "0") {
            mUserUUID = intent.getStringExtra(getString(R.string.sp_loginUserUUID)).toString()
        }

//        animal list spinner adapter
        val animalTypeList = resources.getStringArray(R.array.pashuSalesActivity_animalTypes)
        val animalTypeSpinner = findViewById<Spinner>(R.id.animalTypeSpinner)
        if (animalTypeSpinner != null) {
            val animalTypeAdapter = ArrayAdapter(this, R.layout.spinner_item, animalTypeList)
            animalTypeSpinner.adapter = animalTypeAdapter
            animalTypeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    mAnimalType = animalTypeList[position]
                    animalTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
//        breed type spinner adapter
        val animalBreedList = resources.getStringArray(R.array.pashuSalesActivity_animalBreeds)
        val animalBreedSpinner = findViewById<Spinner>(R.id.animalBreedSpinner)
        if (animalBreedSpinner != null) {
            val animalBreedAdapter = ArrayAdapter(this, R.layout.spinner_item, animalBreedList)
            animalBreedSpinner.adapter = animalBreedAdapter
            animalBreedSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    mAnimalBreed = animalBreedList[position]
                    animalBreedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
//        byaat spinner adapter
        val animalByaatList = resources.getStringArray(R.array.pashuSalesActivity_animalByaat)
        val animalByaatSpinner = findViewById<Spinner>(R.id.animalByaatSpinner)
        if (animalByaatSpinner != null) {
            val byaat_adapter = ArrayAdapter(this, R.layout.spinner_item, animalByaatList)
            animalByaatSpinner.adapter = byaat_adapter
            animalByaatSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    mAnimalByaat = animalByaatList[position]
                    byaat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        val selectPhotoButton1 = findViewById<Button>(R.id.selectPhotoButton1)
        val selectPhotoButton2 = findViewById<Button>(R.id.selectPhotoButton2)
        val selectPhotoButton3 = findViewById<Button>(R.id.selectPhotoButton3)
        val selectPhotoButton4 = findViewById<Button>(R.id.selectPhotoButton4)

        selectPhotoButton1.setOnClickListener {
            setupPermissions()
            mSelectedImage1 = findViewById(R.id.selectedImage1)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
            mSelectedImageOrder.add(mSelectedImage1)
        }
        selectPhotoButton2.setOnClickListener {
            setupPermissions()
            mSelectedImage2 = findViewById(R.id.selectedImage2)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
            mSelectedImageOrder.add(mSelectedImage2)
        }
        selectPhotoButton3.setOnClickListener {
            setupPermissions()
            mSelectedImage3 = findViewById(R.id.selectedImage3)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
            mSelectedImageOrder.add(mSelectedImage3)
        }
        selectPhotoButton4.setOnClickListener {
            setupPermissions()
            mSelectedImage4 = findViewById(R.id.selectedImage4)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
            mSelectedImageOrder.add(mSelectedImage4)
        }

        val uploadDataButton = findViewById<Button>(R.id.pashuSales_submitButton)
        uploadDataButton.setOnClickListener {
            uploadImages()
        }

    }

    private fun uploadImages() {
        var downloadUriList = PashudhanDB.uploadFiles(mImageList, "Animals/")
        if (downloadUriList?.size == mImageList.size) {
            uploadData(downloadUriList)
        } else {
            helper.showSnackbar(
                this,
                rootLayout,
                getString(R.string.pashuSalesActivity_fileUploadError),
                helper.ERROR_STATE
            )
        }
    }

    private fun uploadData(imageUriList: ArrayList<Uri>) {

        mAnimalPrice = findViewById<EditText>(R.id.animalPriceEditText).text.toString()
        mAnimalAge = findViewById<EditText>(R.id.animalAgeEditText).text.toString()

        val animalEntry = hashMapOf<String, Any>(
            "user_uuid" to mUserUUID,
            "animalType" to mAnimalType,
            "animalBreed" to mAnimalBreed,
            "animalAge" to mAnimalAge,
            "animalByaat" to mAnimalByaat,
            "animalMilkQuantity" to mAnimalMilkQuantity,
            "animalMiklCapacity" to mAnimalMilkCapacity,
            "animalPrice" to mAnimalPrice,
            "animalImages" to imageUriList.toArray()
        )

        PashudhanDB.addDocument("animal-details", animalEntry)
            .addOnSuccessListener {
                helper.showSnackbar(
                    this,
                    rootLayout,
                    getString(R.string.pashuSalesActivity_dataUploadSuccess),
                    helper.SUCCESS_STATE
                )
                var handler = Handler()

                handler.postDelayed({
                    val intent = Intent(this, Pashubazar::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
            .addOnFailureListener {
                helper.showSnackbar(
                    this,
                    rootLayout,
                    getString(R.string.tryAgainMessage),
                    helper.ERROR_STATE
                )
            }

        mImageList.clear()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            mImageURI = data?.data
            mImageList.add(mImageURI!!)
            if (!mSelectedImageOrder.isNullOrEmpty()) {
                if (!mImageList.isNullOrEmpty()) {
                    mSelectedImageOrder.last().setImageURI(mImageList.last())
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permission Status", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        val RECORD_REQUEST_CODE = 101
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE
        )
    }

}