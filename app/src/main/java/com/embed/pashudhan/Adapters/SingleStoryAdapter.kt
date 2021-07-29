package com.embed.pashudhan.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import java.util.*

class SingleStoryAdapter(ctx: Context, images: ArrayList<Uri>) : PagerAdapter() {

    private var mContext = ctx
    private var mImageList = images
    private var mLayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var helper = Helper()

    override fun getCount(): Int {
        return mImageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        var itemView: View =
            mLayoutInflater.inflate(R.layout.single_story_card, container, false)
        var imageView: ImageView =
            itemView.findViewById(R.id.singleImageViewStory) as ImageView

        var image = mImageList[position]

        Glide.with(mContext).load(image).placeholder(R.drawable.download).fitCenter()
            .into(imageView)

        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}