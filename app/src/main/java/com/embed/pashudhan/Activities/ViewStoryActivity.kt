package com.embed.pashudhan.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rtugeek.android.colorseekbar.ColorSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar.OnColorChangeListener
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import java.io.File


class ViewStoryActivity : AppCompatActivity() {

    companion object {
        private val TAG = "ViewStory==>"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private lateinit var mImageURI: Uri
    private lateinit var mClickedPhotoEditorView: PhotoEditorView
    private lateinit var mClickedPhotoEditor: PhotoEditor
    private val PashudhanDB = Firebase.firestore
    private lateinit var mUserUUID: String
    private lateinit var mShareButton: Button
    private lateinit var mCloseButton: ImageButton
    private lateinit var mSaveSettingButton: ImageButton
    private lateinit var mAddTextToStoryButton: FloatingActionButton
    private lateinit var mAddStickerToStoryButton: FloatingActionButton
    private lateinit var mDownloadStoryButton: FloatingActionButton
    private lateinit var rootLayout: RelativeLayout
    private lateinit var addTextToStoryEditText: EditText
    private lateinit var colorSlider: ColorSeekBar
    private lateinit var editMode: String
    private lateinit var outputDirectory: File


    private var helper = Helper()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_story_activity_layout)

        mImageURI = Uri.parse(intent.getStringExtra("imageUri").toString())
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID = sharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        mClickedPhotoEditorView = findViewById(R.id.photoEditorView)
        mClickedPhotoEditorView.source.setImageURI(mImageURI)

        rootLayout = findViewById(R.id.viewImageControlLayout)
        mShareButton = findViewById(R.id.shareStoryBtn)
        mShareButton.setOnClickListener {
            saveFileToStorage()
            uploadImage(mImageURI)
        }
        outputDirectory = getOutputDirectory()
        mCloseButton = findViewById(R.id.closeViewImageBtn)
        mSaveSettingButton = findViewById(R.id.saveChangesInEditor)
        mCloseButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        mSaveSettingButton.setOnClickListener {
            if (editMode == "text") {
                Log.d(
                    TAG,
                    addTextToStoryEditText.text.toString() + addTextToStoryEditText.currentTextColor
                )
                addTextToStoryEditText.visibility = View.GONE
                colorSlider.visibility = View.GONE
                mClickedPhotoEditor.addText(
                    addTextToStoryEditText.text.toString(),
                    addTextToStoryEditText.currentTextColor
                )
                mSaveSettingButton.visibility = View.GONE
                editMode = ""
            }
        }

        mClickedPhotoEditor = PhotoEditor.Builder(this, mClickedPhotoEditorView).build()
        mAddTextToStoryButton = findViewById(R.id.addTextToStory)
        addTextToStoryEditText = findViewById(R.id.storyAddTextField)
        colorSlider = findViewById(R.id.colorSlider)
        mAddTextToStoryButton.setOnClickListener {
            editMode = "text"
            addTextToStoryEditText.visibility = View.VISIBLE
            colorSlider.visibility = View.VISIBLE
            colorSlider.setThumbHeight(50f)
            addTextToStoryEditText.requestFocus()
            mSaveSettingButton.visibility = View.VISIBLE
        }

        colorSlider.setOnColorChangeListener(OnColorChangeListener { colorBarPosition, alphaBarPosition, color ->
            addTextToStoryEditText.setTextColor(color)
        })

        mDownloadStoryButton = findViewById(R.id.saveStoryToStorage)
        mDownloadStoryButton.setOnClickListener {
            // Request camera permissions
            if (allPermissionsGranted()) {
                saveFileToStorage()
            } else {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                saveFileToStorage()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun saveFileToStorage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            allPermissionsGranted()
            return
        }

        val fileName = helper.getRandomString(15)
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory, "${fileName}_${System.currentTimeMillis() / 1000}.jpg"
        )
        Log.d(TAG, photoFile.absolutePath)
        mClickedPhotoEditor.saveAsFile(photoFile.absolutePath, object : OnSaveListener {
            override fun onSuccess(imagePath: String) {
                mImageURI = Uri.fromFile(photoFile)
                mClickedPhotoEditorView.source.setImageURI(mImageURI)
                Toast.makeText(
                    this@ViewStoryActivity,
                    "Image Saved at ${photoFile.absolutePath}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(exception: Exception) {
                Log.e("PhotoEditor", "Failed to save Image")
            }
        })
    }

    fun uploadImage(mImageURI: Uri) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var fileName = helper.getRandomString(15)
        var imageRef =
            storageRef.child("StoryImages/${fileName}_${System.currentTimeMillis() / 1000}.jpg")


        var uploadTask = imageRef.putFile(mImageURI)
        uploadTask.addOnProgressListener {
            findViewById<LinearLayout>(R.id.story_ProgressLayout).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.viewImageControlLayout).visibility = View.GONE
        }.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d(TAG, it.toString())
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                uploadData("$downloadUri")
                Log.d(TAG, "Uploaded image")
            } else {
                Log.d(TAG, "mUploadImages error")
            }
        }
    }

    private fun uploadData(downloadUri: String) {
        Log.d(TAG, "Data Uploading now")
        var mStoryRootLayout = findViewById<RelativeLayout>(R.id.clickedImagesRootLayout)

        val storyItem = hashMapOf(
            "imageUri" to downloadUri,
            "timestamp" to "${System.currentTimeMillis() / 1000}",
            "storyComments" to "0",
            "likes" to "0",
        )

        var storyItemToArray = ArrayList<HashMap<String, String>>()
        storyItemToArray.add(storyItem)

        val storiesList = hashMapOf(
            "storiesList" to storyItemToArray
        )

        Log.d(TAG, "$storyItem")

        var newStoryRef = PashudhanDB.collection("Stories").document(mUserUUID)
        newStoryRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    newStoryRef.update("storiesList", FieldValue.arrayUnion(storyItem))
                        .addOnSuccessListener {

                            findViewById<ProgressBar>(R.id.story_loadDataProgressBar).visibility =
                                View.GONE
                            findViewById<TextView>(R.id.story_progressDescription).visibility =
                                View.GONE
                            findViewById<ImageView>(R.id.story_successIcon).visibility =
                                View.VISIBLE

                            helper.showSnackbar(
                                this,
                                mStoryRootLayout,
                                getString(R.string.pashuSalesActivity_dataUploadSuccess),
                                helper.SUCCESS_STATE
                            )
                            var handler = Handler()

                            handler.postDelayed({
                                val intent = Intent(this, PashuStoryActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 1000)
                        }
                        .addOnFailureListener {
                            findViewById<LinearLayout>(R.id.story_ProgressLayout).visibility =
                                View.GONE
                            helper.showSnackbar(
                                this,
                                mStoryRootLayout,
                                getString(R.string.tryAgainMessage),
                                helper.ERROR_STATE
                            )
                            val intent = Intent(this, AddStoryActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                } else {
                    newStoryRef.set(storiesList)
                        .addOnSuccessListener {
                            helper.showSnackbar(
                                this,
                                mStoryRootLayout,
                                getString(R.string.pashuSalesActivity_dataUploadSuccess),
                                helper.SUCCESS_STATE
                            )
                            findViewById<ProgressBar>(R.id.story_loadDataProgressBar).visibility =
                                View.GONE
                            findViewById<TextView>(R.id.story_progressDescription).visibility =
                                View.GONE
                            findViewById<ImageView>(R.id.story_successIcon).visibility =
                                View.VISIBLE
                            var handler = Handler()

                            handler.postDelayed({
                                setContentView(R.layout.pashu_story_activity_layout)
                            }, 1000)
                        }
                        .addOnFailureListener {
                            findViewById<LinearLayout>(R.id.story_ProgressLayout).visibility =
                                View.GONE
                            setContentView(R.layout.pashu_story_activity_layout)
                            helper.showSnackbar(
                                this,
                                mStoryRootLayout,
                                getString(R.string.tryAgainMessage),
                                helper.ERROR_STATE
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }


    }


}