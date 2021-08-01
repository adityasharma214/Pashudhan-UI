package com.embed.pashudhan.Adapters


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.StoryData
import com.embed.pashudhan.R
import jp.shts.android.storiesprogressview.StoriesProgressView
import java.util.*

class StoryPagerAdapter(ctx: Context, stories: ArrayList<StoryData>) :
    RecyclerView.Adapter<StoryPagerAdapter.StoryPagerViewHolder>() {

    companion object {
        private val TAG = "StoryAdapter==>"
    }

    private var mContext = ctx
    private var mStoriesList = stories
    private lateinit var imageUriList: ArrayList<Uri>
    var pressTime = 0L
    var limit = 500L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagerViewHolder {
        var view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_container_copy, parent, false)
        return StoryPagerViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: StoryPagerViewHolder, position: Int) {
        var story = mStoriesList[position]
        imageUriList = arrayListOf<Uri>()

        for (item in story.storiesList!!) {
            imageUriList.add(Uri.parse(item.imageUri))
        }

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
        var image = imageUriList[position]
        holder.storiesView.setStoriesCount(imageUriList.size)
        holder.storiesView.setStoryDuration(3000L)
        var storyListener = StoryListener(position, holder, imageUriList)
        holder.storiesView.setStoriesListener(storyListener)
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
    }

    override fun getItemCount(): Int {
        return mStoriesList.size
    }


    class StoryPagerViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val singleStoryImageView: ImageView = itemView.findViewById(R.id.story_image)
        val reverseBtn: View = itemView.findViewById(R.id.reverse)
        val skipBtn: View = itemView.findViewById(R.id.skip)
        val storiesView: StoriesProgressView = itemView.findViewById(R.id.storiesprogressview)
    }


}