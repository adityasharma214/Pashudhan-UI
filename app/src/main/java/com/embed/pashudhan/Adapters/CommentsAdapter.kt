package com.embed.pashudhan.Adapters

import android.content.Context
import android.text.format.DateUtils.getRelativeTimeSpanString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.CommentsData
import com.embed.pashudhan.R

class CommentsAdapter(
    ctx: Context, commentsList: ArrayList<CommentsData>
) : RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    private var mContext = ctx
    private var mCommentsList = commentsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_card, parent, false)
        return MyViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var comment = mCommentsList[position]
        Glide.with(mContext).load(comment.profileImage).placeholder(R.drawable.user_placeholder)
            .into(holder.userProfileImage)
        holder.userFullName.text = "${comment.firstName} ${comment.lastName}"
        holder.commentContent.text = comment.commentContent
        holder.commentTimestamp.text = getRelativeTimeSpanString(
            comment.timestamp?.toLong()!! * 1000,
            System.currentTimeMillis(),
            0
        )

    }

    override fun getItemCount(): Int {
        return mCommentsList.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var userProfileImage: ImageView = itemview.findViewById(R.id.user_profile_image_comment)
        var userFullName: TextView = itemview.findViewById(R.id.comment_user_fullname)
        var commentContent: TextView = itemview.findViewById(R.id.comment_content)
        var commentTimestamp: TextView = itemview.findViewById(R.id.comment_time_posted)
    }
}