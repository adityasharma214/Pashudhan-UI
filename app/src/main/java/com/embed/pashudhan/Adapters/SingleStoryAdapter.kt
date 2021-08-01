package com.embed.pashudhan.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.R
import java.util.*

class SingleStoryAdapter(ctx: Context, images: ArrayList<Uri>) :
    RecyclerView.Adapter<SingleStoryAdapter.SingleStoryViewHolder>() {

    private var mContext = ctx
    private var mImageList = images

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleStoryViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.single_story_card, parent, false)
        return SingleStoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleStoryViewHolder, position: Int) {
        var image = mImageList[position]
        Glide.with(mContext).load(image).placeholder(R.drawable.download).fitCenter()
            .into(holder.singleStoryImageView)
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    class SingleStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singleStoryImageView: ImageView = itemView.findViewById(R.id.story_image)
    }
}