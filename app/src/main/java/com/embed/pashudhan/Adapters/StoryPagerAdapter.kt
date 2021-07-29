package com.embed.pashudhan.Adapters


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.embed.pashudhan.DataModels.StoryData
import com.embed.pashudhan.R
import java.util.*

class StoryPagerAdapter(ctx: Context, stories: ArrayList<StoryData>) :
    RecyclerView.Adapter<StoryPagerAdapter.StoryPagerViewHolder>() {

    companion object {
        private val TAG = "StoryAdapter==>"
    }

    private var mContext = ctx
    private var mStoriesList = stories

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagerViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.story_container, parent, false)
        return StoryPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryPagerViewHolder, position: Int) {
        var story = mStoriesList[position]
        var imageUriList = arrayListOf<Uri>()
        for (item in story.storiesList!!) {
            imageUriList.add(Uri.parse(item.imageUri))
        }
        holder.storiesViewPager.adapter = SingleStoryAdapter(mContext, imageUriList)
    }

    override fun getItemCount(): Int {
        return mStoriesList.size
    }


    class StoryPagerViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val storiesViewPager: ViewPager = itemView.findViewById(R.id.singleStoryContainer)

    }

}