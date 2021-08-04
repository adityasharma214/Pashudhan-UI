package com.embed.pashudhan.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.embed.pashudhan.Adapters.*
import com.embed.pashudhan.DataModels.StoryData
import com.embed.pashudhan.DataModels.StoryItem
import com.embed.pashudhan.DataModels.StoryUserDataModel
import com.embed.pashudhan.DataModels.users
import com.embed.pashudhan.R
import com.google.firebase.firestore.*


class PashuStoryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PashuStoryActivity==>"
    }

    private lateinit var mStoryPageAdater: StoryPagerAdapter
    private lateinit var mStoryPageView: ViewPager2
    private lateinit var mCameraBtn: ImageButton
    private lateinit var PashudhanDB: FirebaseFirestore
    private lateinit var mStoriesList: ArrayList<StoryData>
    private lateinit var mStoriesUsersList: ArrayList<StoryUserDataModel>
    private lateinit var mUserUUID: String
    private lateinit var mUserFullNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashu_story_activity_layout)

        val checkLoginSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID =
            checkLoginSharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        mStoryPageView = findViewById(R.id.storiesHolderViewPager)
//        mStoriesList = arrayListOf()
        mStoriesUsersList = arrayListOf()
        mStoryPageAdater =
            StoryPagerAdapter(
                this@PashuStoryActivity,
                mStoriesUsersList,
                this::changeStory,
                this::changeName
            )

        mStoryPageView.adapter = mStoryPageAdater
        mStoryPageView.orientation = ViewPager2.ORIENTATION_VERTICAL

        mUserFullNameTextView = findViewById(R.id.userFullNameTextView)

        mCameraBtn = findViewById(R.id.cameraBtn)
        mCameraBtn.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        EventChangeListener()
    }

    fun changeName(name: String) {
        mUserFullNameTextView.text = name
    }

    fun changeStory(i: Int) {
        mStoryPageView.currentItem = mStoryPageView.currentItem + i
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
                    if (value?.documentChanges?.isNotEmpty()!!) {
                        for (dc: DocumentChange in value.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                var docId = dc.document.id
                                Log.d(TAG, docId)
                                var storyDocument = dc.document.toObject(StoryData::class.java)
                                val docRef = PashudhanDB.collection("users").document(docId)
                                docRef.get()
                                    .addOnSuccessListener { userDocument ->
                                        if (storyDocument != null) {

                                            var storyList = storyDocument.storiesList!!
                                            var newList = arrayListOf<StoryItem>()
                                            storyList.forEach {
                                                var storyTimestamp = it.timestamp?.toLong()
                                                var currentTimestamp =
                                                    System.currentTimeMillis() / 1000

                                                var durationInHours =
                                                    (currentTimestamp.minus(storyTimestamp!!)) / 3600

                                                if (durationInHours < 24) {
                                                    newList.add(it)
                                                }
                                            }
                                            if (newList.size > 0) {
                                                var newDoc = StoryUserDataModel()
                                                newDoc.storiesList = newList
                                                newDoc.userInfo =
                                                    userDocument.toObject(users::class.java)
                                                Log.d(TAG, "$newDoc")
                                                mStoriesUsersList.add(newDoc)
//                                                storyDocument.storiesList = newList
//                                                mStoriesList.add(storyDocument)

                                                mStoryPageAdater.notifyDataSetChanged()
                                            }
                                        } else {
                                            Log.d(TAG, "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->

                                    }


//                            mStoriesList.add(document)
                            }
                        }
                    }
                }
            })

    }
}


