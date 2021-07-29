package com.embed.pashudhan.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.embed.pashudhan.Adapters.*
import com.embed.pashudhan.DataModels.StoryData
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class PashuStoryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PashuStoryActivity==>"
    }

    private lateinit var mStoryPageAdater: StoryPagerAdapter
    private lateinit var mStoryPageView: ViewPager2
    private lateinit var mCameraBtn: ImageButton
    private lateinit var PashudhanDB: FirebaseFirestore
    private lateinit var mStoriesList: ArrayList<StoryData>
    private lateinit var mClickedImagesList: ArrayList<Bitmap>
    private lateinit var mClickedImageView: ImageView
    private lateinit var mUserUUID: String
    private var helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashu_story_activity_layout)

        val checkLoginSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID =
            checkLoginSharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        mStoryPageView = findViewById(R.id.storiesHolderViewPager)
        mStoriesList = arrayListOf()
        mStoryPageAdater = StoryPagerAdapter(this@PashuStoryActivity, mStoriesList)

        mStoryPageView.adapter = mStoryPageAdater
        mStoryPageView.orientation = ViewPager2.ORIENTATION_VERTICAL

        mClickedImagesList = arrayListOf()
        EventChangeListener()

        mCameraBtn = findViewById(R.id.cameraBtn)

        mCameraBtn.setOnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicture, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED && requestCode == 0) {
            if (resultCode == RESULT_OK && data != null) {
                val clickedImage = data.extras!!["data"] as Bitmap?
                setContentView(R.layout.clicked_images_layout)
                mClickedImageView = findViewById(R.id.clickedImageView)
                mClickedImageView.setImageBitmap(clickedImage)
                var shareImageBtn = findViewById<Button>(R.id.shareStoryBtn)

                shareImageBtn.setOnClickListener {
                    uploadImage(clickedImage!!)
                }
            }
        }
    }

    fun uploadImage(clickedImage: Bitmap) {

        val storage = Firebase.storage
        var storageRef = storage.reference

        var imageRef =
            storageRef.child("StoryImages/${mUserUUID}_${(System.currentTimeMillis() / 1000)}")
        val baos = ByteArrayOutputStream()
        clickedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef.putBytes(data)
        uploadTask.addOnProgressListener {
            findViewById<LinearLayout>(R.id.story_ProgressLayout).visibility = View.VISIBLE
            mClickedImageView.visibility = View.GONE
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


    private fun EventChangeListener() {
        PashudhanDB = FirebaseFirestore.getInstance()
        PashudhanDB.collection("Stories")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            mStoriesList.add(dc.document.toObject(StoryData::class.java))
                        }
                    }
                    mStoryPageAdater.notifyDataSetChanged()
                }
            })

    }
}


