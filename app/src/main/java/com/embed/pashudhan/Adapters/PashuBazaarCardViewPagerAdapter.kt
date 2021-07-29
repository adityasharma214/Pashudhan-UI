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
import com.embed.pashudhan.R
import java.util.*

class PashuBazaarCardViewPagerAdapter(ctx: Context, images: ArrayList<Uri>) : PagerAdapter() {

    private var mContext = ctx
    private var mImageList = images
    private var mLayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return mImageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as LinearLayout)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var itemView: View =
            mLayoutInflater.inflate(R.layout.pashubazaar_card_viewpager_item, container, false)
        var imageView: ImageView =
            itemView.findViewById(R.id.pashubazaar_card_viewPager_itemImage) as ImageView

        Glide.with(mContext).load(mImageList[position]).placeholder(R.drawable.download)
            .into(imageView)
//        imageView.setImageResource(mImageList[position]);
        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}