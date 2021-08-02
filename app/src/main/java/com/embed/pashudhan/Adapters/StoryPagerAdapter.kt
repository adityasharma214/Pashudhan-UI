package com.embed.pashudhan.Adapters


import android.annotation.SuppressLint
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.StoryItem
import com.embed.pashudhan.DataModels.StoryUserDataModel
import com.embed.pashudhan.R
import com.google.firebase.firestore.FirebaseFirestore
import jp.shts.android.storiesprogressview.StoriesProgressView
import java.util.*

class StoryPagerAdapter(
    activity: AppCompatActivity,
    stories: ArrayList<StoryUserDataModel>,
    changeStory: ((Int) -> Unit),
    changeName: ((String) -> Unit)
) :
    RecyclerView.Adapter<StoryPagerAdapter.StoryPagerViewHolder>() {

    companion object {
        private val TAG = "StoryAdapter==>"
    }

    private var mContext = activity.applicationContext
    private var mActivity = activity
    private var mStoriesList = stories
    private var mChangeStory = changeStory
    private var mChangeName = changeName
    private lateinit var imageUriList: ArrayList<Uri>
    private lateinit var mHolder: StoryPagerViewHolder
    private lateinit var mStory: StoryUserDataModel
    private lateinit var mUserUUID: String
    private var mCounter: MutableLiveData<Int> = MutableLiveData()
    private var mLiked: MutableLiveData<Boolean> = MutableLiveData()
    private var mStoryItem: MutableLiveData<StoryItem> = MutableLiveData()
    var pressTime = 0L
    var limit = 500L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagerViewHolder {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(parent.context)
        mUserUUID = sharedPref.getString(parent.resources.getString(R.string.sp_loginUserUUID), "0")
            .toString()
        var view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_container_copy, parent, false)
        return StoryPagerViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: StoryPagerViewHolder, position: Int) {
        mHolder = holder
        mStory = mStoriesList[position]
        mCounter.value = 0
        mStoryItem.value = mStory.storiesList?.get(mCounter.value!!)
        mLiked.value = false
        Log.d(TAG, "$mStory")
        imageUriList = arrayListOf()


        for (item in mStory.storiesList!!) {
            imageUriList.add(Uri.parse(item.imageUri))
        }
        Log.d(TAG, "$imageUriList")
        val onTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    mHolder.storiesView.pause()
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    mHolder.storiesView.resume()
                    return@OnTouchListener limit < now - pressTime
                }
            }
            false
        }

        var image = imageUriList[mCounter.value!!]
        var firstName = mStoriesList[position].userInfo?.firstName!!
        var lastName = mStoriesList[position].userInfo?.lastName!!
        mChangeName("$firstName $lastName")
        mHolder.storiesView.setStoriesCount(imageUriList.size)
        mHolder.storiesView.setStoryDuration(3000L)


        mHolder.storiesView.setStoriesListener(object : StoriesProgressView.StoriesListener {
            override fun onNext() {
                mCounter.value = mCounter.value!! + 1
                Glide.with(mContext).load(imageUriList[mCounter.value!!])
                    .placeholder(R.drawable.download)
                    .fitCenter()
                    .into(mHolder.singleStoryImageView)

            }

            override fun onPrev() {
                if (mCounter.value!! > 0) {
                    mCounter.value = mCounter.value!! - 1
                    Glide.with(mContext).load(imageUriList[mCounter.value!!])
                        .placeholder(R.drawable.download)
                        .fitCenter()
                        .into(mHolder.singleStoryImageView)
                }
            }

            override fun onComplete() {
                mChangeStory(+1)
            }

        })

        mHolder.likeButton.setOnClickListener {
            var newStoryItem = mStoryItem.value
            if (mLiked.value!!) {
                newStoryItem?.likes?.remove(mUserUUID)
                mLiked.value = false
            } else {
                newStoryItem?.likes?.add(mUserUUID)
                mLiked.value = true
            }
            mStoryItem.value = newStoryItem
            var storiesList = mStoriesList[position].storiesList
            storiesList?.set(mCounter.value!!, newStoryItem!!)
            changeLike(storiesList!!)
        }

        mStoryItem.observe(mActivity, {
            mHolder.likeCountTextView.text = "${it?.likes?.size}"
        })

        mCounter.observe(mActivity, {
            var observeredCounter = it
            mStoryItem.value = mStory.storiesList?.get(observeredCounter)!!
            mLiked.value = mStoryItem.value?.likes?.contains(mUserUUID)!!
        })

        mLiked.observe(mActivity, {
            if (it) {
                mHolder.likeButton.setImageDrawable(mHolder.itemView.resources.getDrawable(R.drawable.ic_favourite_filled))
            } else {
                mHolder.likeButton.setImageDrawable(mHolder.itemView.resources.getDrawable(R.drawable.ic_favourite_outline))
            }
        })

        mHolder.storiesView.startStories()
        Glide.with(mContext).load(image).placeholder(R.drawable.download).fitCenter()
            .into(mHolder.singleStoryImageView)
        mHolder.reverseBtn.setOnClickListener {
            mHolder.storiesView.reverse()
        }
        mHolder.reverseBtn.setOnTouchListener(onTouchListener)
        mHolder.skipBtn.setOnClickListener {
            mHolder.storiesView.skip()
        }
        mHolder.skipBtn.setOnTouchListener(onTouchListener)


    }

    private fun changeLike(storiesList: ArrayList<StoryItem>) {

        var db = FirebaseFirestore.getInstance()
        db.collection("Stories").document(mUserUUID)
            .update(
                mapOf(
                    "storiesList" to storiesList
                )
            ).addOnSuccessListener {}
    }


    override fun getItemCount(): Int {
        return mStoriesList.size
    }

    class StoryPagerViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val singleStoryImageView: ImageView = itemview.findViewById(R.id.story_image)
        val reverseBtn: View = itemview.findViewById(R.id.reverse)
        val skipBtn: View = itemview.findViewById(R.id.skip)
        val storiesView: StoriesProgressView = itemview.findViewById(R.id.storiesprogressview)
        val likeButton: ImageButton = itemview.findViewById(R.id.likeStoryButton)
        val likeCountTextView: TextView = itemview.findViewById(R.id.likeStoryCount)
        val commentButton: ImageButton = itemview.findViewById(R.id.commentStoryButton)
        val commentTextView: TextView = itemview.findViewById(R.id.commentStoryCount)
    }


}
