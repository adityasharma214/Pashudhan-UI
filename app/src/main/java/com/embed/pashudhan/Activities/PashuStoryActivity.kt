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
    private lateinit var mUserUUID: String

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

        mCameraBtn = findViewById(R.id.cameraBtn)

        mCameraBtn.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        EventChangeListener()
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
                            var document = dc.document.toObject(StoryData::class.java)
                            var storyList = document.storiesList!!
                            var newList = arrayListOf<StoryItem>()
                            storyList.forEach {
                                var storyTimestamp = it.timestamp?.toLong()
                                var currentTimestamp = System.currentTimeMillis() / 1000

                                var durationInHours =
                                    (currentTimestamp.minus(storyTimestamp!!)) / 3600

                                if (durationInHours < 24) {
                                    newList.add(it)
                                }
                            }
                            if (newList.size > 0) {
                                document.storiesList = newList
                                mStoriesList.add(document)
                            }
                        }
                    }
                    mStoryPageAdater.notifyDataSetChanged()
                }
            })

    }
}


