package com.embed.pashudhan.Adapters


import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.Activities.PashuStoryActivity
import com.embed.pashudhan.DataModels.Pashubazaar
import com.embed.pashudhan.DataModels.StoryDataModel
import com.embed.pashudhan.R

class StoryAdapter(
    private var storylist: ArrayList<StoryDataModel>,
    private val mContext: Context
) :
    RecyclerView.Adapter<StoryAdapter.MyViewHolder>() {

    companion object {
        private val TAG = "StoryAdapter==>"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemview =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_card, parent, false)
        return MyViewHolder(itemview)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val storyData: StoryDataModel = storylist[position]
        val imageUri = Uri.parse(storyData.img?.get(1))
        Log.d(StoryAdapter.TAG, imageUri.toString())
        Glide.with(mContext).load(imageUri).placeholder(R.drawable.download)
            .into(holder.storyimages)

    }

    override fun getItemCount(): Int {
        return storylist.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val storyimages: ImageView = itemview.findViewById(R.id.story_image)

    }

}