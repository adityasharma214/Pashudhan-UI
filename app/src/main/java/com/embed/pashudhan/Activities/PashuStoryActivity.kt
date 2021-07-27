package com.embed.pashudhan.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.embed.pashudhan.Adapters.ViewPagerAdapter
import com.embed.pashudhan.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter


class PashuStoryActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    val REQUEST_CODE = 200
    val PERMISSION_ALL = 1
    val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val db = Firebase.firestore
    private lateinit var share_btn : Button
    private lateinit var add_more_btn : Button
    // creating object of ViewPager
    var mViewPager: ViewPager? = null
    var images = ArrayList<Int>()
    var images_bitmap = ArrayList<Bitmap>()
    // Creating Object of ViewPagerAdapter
    private var mViewPagerAdapter: ViewPagerAdapter? = null
    var uploaded_imges = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashu_story_activity_layout)


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.feed_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.whatsapp_share -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        R.id.open_camera -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            if (!hasPermissions(this, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            }
            Log.d("CameraButton", "camera Button is clicked")
            selectImage(this)

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog,
                                                                    item ->
            if (options[item] == "Take Photo") {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    setContentView(R.layout.add_feed)
                    images.add(0)
                    images_bitmap.add(selectedImage!!)
//                    imageView = findViewById(R.id.imageView10)
//                    imageView.setImageBitmap(selectedImage)
                    // Initializing the ViewPager Object

                    // Initializing the ViewPager Object
                    mViewPager = findViewById<View>(R.id.viewPagerMain) as ViewPager

                    // Initializing the ViewPagerAdapter

                    // Initializing the ViewPagerAdapter
                    mViewPagerAdapter = ViewPagerAdapter(this@PashuStoryActivity, images, images_bitmap)

                    // Adding the Adapter to the ViewPager

                    // Adding the Adapter to the ViewPager
                    mViewPager!!.adapter = mViewPagerAdapter
                    share_btn = findViewById(R.id.share_btn)
                    share_btn.setOnClickListener {
                        upload_data()
                    }
                    add_more_btn = findViewById(R.id.add_more_btn)
                    add_more_btn.setOnClickListener {
                        selectImage(this)
                    }
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            setContentView(R.layout.add_feed)
                            try {
                                selectedImage?.let {
                                    if(Build.VERSION.SDK_INT < 28) {
                                        val bitmap = MediaStore.Images.Media.getBitmap(
                                            this.contentResolver,
                                            selectedImage
                                        )
                                        images.add(0)
                                        images_bitmap.add(bitmap)
                                    } else {
                                        val source = ImageDecoder.createSource(this.contentResolver, selectedImage)
                                        val bitmap = ImageDecoder.decodeBitmap(source)
                                        images.add(0)
                                        images_bitmap.add(bitmap)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
//                            var bitmap =
//                                BitmapFactory.decodeFile(selectedImage.toString())


                            // Initializing the ViewPager Object
                            mViewPager = findViewById<View>(R.id.viewPagerMain) as ViewPager
                            // Initializing the ViewPagerAdapter
                            mViewPagerAdapter = ViewPagerAdapter(
                                this@PashuStoryActivity,
                                images,
                                images_bitmap
                            )
                            // Adding the Adapter to the ViewPager
                            mViewPager!!.adapter = mViewPagerAdapter
                            if (!hasPermissions(this, *PERMISSIONS)) {
                                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
                            }
//                            imageView.setImageURI(selectedImage)
                            share_btn = findViewById(R.id.share_btn)
                            share_btn.setOnClickListener {
                                upload_data()
                            }
                            add_more_btn = findViewById(R.id.add_more_btn)
                            add_more_btn.setOnClickListener {
                                selectImage(this)
                            }
                            cursor.close()
                        }
                    }
                }
            }
        }
    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun upload_data(){

        for (elements in images_bitmap) {
            var rand_str = getRandomString(15)
            val storage = Firebase.storage
            var storageRef = storage.reference
            var timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            } else {
                System.currentTimeMillis()
            }
            var imagesRef: StorageReference? =
                storageRef.child("kahani_images/${rand_str + timestamp}")
            // [START upload_memory]
            // Get the data from an ImageView as bytes

            val bitmap = elements
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = imagesRef?.putBytes(data)
            uploaded_imges.add(imagesRef.toString())
            uploadTask?.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.d("ErrorUpload", it.toString())
            }?.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...

            }
        }
            val checkLoginSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val mUserUUID = checkLoginSharedPref.getString(getString(R.string.sp_loginUserUUID), "0")
            val kahani = hashMapOf(
                "comments" to 0,
                "likes" to 0,
                "img" to uploaded_imges,
                "user_id" to mUserUUID,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("kahani")
                .add(kahani)
                .addOnSuccessListener {DocumentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${DocumentReference.id}")}
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        uploaded_imges.clear()
        images_bitmap.clear()
        images.clear()
        // [END upload_memory]
    setContentView(R.layout.pashu_story_activity_layout)

    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "FirebaseDatabase"
    }

}
