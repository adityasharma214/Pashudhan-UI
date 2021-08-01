package com.embed.pashudhan.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewStoryActivity : AppCompatActivity() {

    companion object {
        private val TAG = "ViewStory==>"
    }

    private lateinit var mImageURI: Uri
    private lateinit var mClickedImageView: ImageView
    private val PashudhanDB = Firebase.firestore
    private lateinit var mUserUUID: String
    private lateinit var mShareButton: Button

    private var helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_story_activity_layout)

        mImageURI = Uri.parse(intent.getStringExtra("imageUri").toString())
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID = sharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        mClickedImageView = findViewById(R.id.clickedImageView)
        mClickedImageView.setImageURI(null)
        mClickedImageView.setImageURI(mImageURI)

        mShareButton = findViewById(R.id.shareStoryBtn)
        mShareButton.setOnClickListener {
            uploadImage(mImageURI)
        }

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