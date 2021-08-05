package com.embed.pashudhan.Adapters


import android.annotation.SuppressLint
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.StoryItem
import com.embed.pashudhan.DataModels.StoryUserDataModel
import com.embed.pashudhan.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private lateinit var mUserFirstName: String
    private lateinit var mUserLastName: String
    private lateinit var mUserProfileImage: String
    private var mCounter: MutableLiveData<Int> = MutableLiveData()
    private var mLiked: MutableLiveData<Boolean> = MutableLiveData()
    private var mStoryItem: MutableLiveData<StoryItem> = MutableLiveData()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private lateinit var bottomSheet: RelativeLayout
    var pressTime = 0L
    var limit = 500L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagerViewHolder {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(parent.context)
        mUserUUID = sharedPref.getString(parent.resources.getString(R.string.sp_loginUserUUID), "0")
            .toString()
        mUserFirstName =
            sharedPref.getString(parent.resources.getString(R.string.sp_userFirstName), "0")
                .toString()
        mUserLastName =
            sharedPref.getString(parent.resources.getString(R.string.sp_userLastName), "0")
                .toString()
        mUserProfileImage =
            sharedPref.getString(parent.resources.getString(R.string.sp_profileImage), "0")
                .toString()
        var view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_container_copy, parent, false)
        return StoryPagerViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "LogNotTimber")
    override fun onBindViewHolder(holder: StoryPagerViewHolder, position: Int) {
        var storyPerUser = mStoriesList[position]
        imageUriList = arrayListOf()
        for (item in storyPerUser.storiesList!!) {
            imageUriList.add(Uri.parse(item.imageUri))
        }
        mCounter.value = 0
        mStoryItem.value = storyPerUser.storiesList?.get(mCounter.value!!)
        val onTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    holder.storiesView.pause()
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    holder.storiesView.resume()
                    return@OnTouchListener limit < now - pressTime
                }
            }
            false
        }

        holder.storiesView.setStoriesCount(imageUriList.size)
        holder.storiesView.setStoryDuration(3000L)
        var image = imageUriList[mCounter.value!!]
        holder.storiesView.setStoriesListener(object : StoriesProgressView.StoriesListener {
            override fun onNext() {
                mCounter.value = mCounter.value!! + 1
                Glide.with(mContext).load(imageUriList[mCounter.value!!])
                    .placeholder(R.drawable.download)
                    .fitCenter()
                    .into(holder.singleStoryImageView)

            }

            override fun onPrev() {
                if (mCounter.value!! > 0) {
                    mCounter.value = mCounter.value!! - 1
                    Glide.with(mContext).load(imageUriList[mCounter.value!!])
                        .placeholder(R.drawable.download)
                        .fitCenter()
                        .into(holder.singleStoryImageView)
                }
            }

            override fun onComplete() {
                mChangeStory(+1)
            }
        })

        holder.storiesView.startStories()

        Glide.with(mContext).load(image).placeholder(R.drawable.download).fitCenter()
            .into(holder.singleStoryImageView)

        holder.reverseBtn.setOnClickListener {
            holder.storiesView.reverse()
        }
        holder.reverseBtn.setOnTouchListener(onTouchListener)
        holder.skipBtn.setOnClickListener {
            holder.storiesView.skip()
        }
        holder.skipBtn.setOnTouchListener(onTouchListener)


        // Observers
        mCounter.observe(mActivity, {
            mStoryItem.value = storyPerUser.storiesList!![it!!]
        })

        mStoryItem.observe(mActivity, {
            Log.d(TAG, it.toString())
            mChangeName(it?.name!!)
        })
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


//@SuppressLint("ClickableViewAccessibility")
//override fun onBindViewHolder(holder: StoryPagerAdapter.StoryPagerViewHolder, position: Int) {
//    mStory = mStoriesList[position]
//
//    mCounter.value = 0
//    mStoryItem.value = mStory.storiesList?.get(mCounter.value!!)
//    mLiked.value = false
//    Log.d(StoryPagerAdapter.TAG, "$mStory")
//    imageUriList = arrayListOf()
//
//    bottomSheet = mHolder.itemView.findViewById(R.id.comments_bottom_sheet)
//    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//
//    for (item in mStory.storiesList!!) {
//        imageUriList.add(Uri.parse(item.imageUri))
//    }
//    Log.d(StoryPagerAdapter.TAG, "$imageUriList")
//    val onTouchListener = View.OnTouchListener { v, event ->
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                pressTime = System.currentTimeMillis()
//                mHolder.storiesView.pause()
//                return@OnTouchListener false
//            }
//            MotionEvent.ACTION_UP -> {
//                val now = System.currentTimeMillis()
//                mHolder.storiesView.resume()
//                return@OnTouchListener limit < now - pressTime
//            }
//        }
//        false
//    }
//
//    var image = imageUriList[mCounter.value!!]
//    var firstName = mStoriesList[position].userInfo?.firstName!!
//    var lastName = mStoriesList[position].userInfo?.lastName!!
//    mChangeName("$firstName $lastName")
//    mHolder.storiesView.setStoriesCount(imageUriList.size)
//    mHolder.storiesView.setStoryDuration(3000L)
//
//
//    mHolder.storiesView.setStoriesListener(object : StoriesProgressView.StoriesListener {
//        override fun onNext() {
//            mCounter.value = mCounter.value!! + 1
//            Glide.with(mContext).load(imageUriList[mCounter.value!!])
//                .placeholder(R.drawable.download)
//                .fitCenter()
//                .into(mHolder.singleStoryImageView)
//
//        }
//
//        override fun onPrev() {
//            if (mCounter.value!! > 0) {
//                mCounter.value = mCounter.value!! - 1
//                Glide.with(mContext).load(imageUriList[mCounter.value!!])
//                    .placeholder(R.drawable.download)
//                    .fitCenter()
//                    .into(mHolder.singleStoryImageView)
//            }
//        }
//
//        override fun onComplete() {
//            mChangeStory(+1)
//        }
//
//    })
//
//    mHolder.likeButton.setOnClickListener {
//        var newStoryItem = mStoryItem.value
//        if (mLiked.value!!) {
//            newStoryItem?.likes?.remove(mUserUUID)
//            mLiked.value = false
//        } else {
//            newStoryItem?.likes?.add(mUserUUID)
//            mLiked.value = true
//        }
//        mStoryItem.value = newStoryItem
//        var storiesList = mStoriesList[position].storiesList
//        storiesList?.set(mCounter.value!!, newStoryItem!!)
//        updateLike(storiesList!!, mStoriesList[position].id!!)
//    }
//    mHolder.storiesView.startStories()
//    Glide.with(mContext).load(image).placeholder(R.drawable.download).fitCenter()
//        .into(mHolder.singleStoryImageView)
//    mHolder.reverseBtn.setOnClickListener {
//        mHolder.storiesView.reverse()
//    }
//    mHolder.reverseBtn.setOnTouchListener(onTouchListener)
//    mHolder.skipBtn.setOnClickListener {
//        mHolder.storiesView.skip()
//    }
//    mHolder.skipBtn.setOnTouchListener(onTouchListener)
//
//    mHolder.commentButton.setOnClickListener {
//        mHolder.storiesView.pause()
//        CommentsFragment.newInstance(
//            mStoriesList,
//            position,
//            mCounter.value!!
//        ).show(mActivity.supportFragmentManager, CommentsFragment.TAG)
//    }
//
//
//    mStoryItem.observe(mActivity, {
//        Log.d(StoryPagerAdapter.TAG, it.toString())
//        mHolder.likeCountTextView.text = "${it?.likes?.size}"
//        mHolder.commentTextView.text = if (it.comments == null) "0" else it.comments?.size.toString()
//    })
//
//    mCounter.observe(mActivity, {
//        var observeredCounter = it
//        mStoryItem.value = mStory.storiesList?.get(observeredCounter)!!
//        mLiked.value = mStoryItem.value?.likes?.contains(mUserUUID)!!
//    })
//
//    mLiked.observe(mActivity, {
//        if (it) {
//            mHolder.likeButton.setImageDrawable(mHolder.itemView.resources.getDrawable(R.drawable.ic_favourite_filled))
//        } else {
//            mHolder.likeButton.setImageDrawable(mHolder.itemView.resources.getDrawable(R.drawable.ic_favourite_outline))
//        }
//    })
//}
//
//
//private fun updateLike(storiesList: ArrayList<StoryItem>, docId: String) {
//
//    var db = FirebaseFirestore.getInstance()
//    db.collection("Stories").document(docId)
//        .update(
//            mapOf(
//                "storiesList" to storiesList
//            )
//        ).addOnSuccessListener {}
//}
