package com.embed.pashudhan.Adapters


import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.embed.pashudhan.R
import java.util.*


internal class ClickedImagesViewPagerAdapter(var ctx: Context, var imagesList: ArrayList<Bitmap>) :
    PagerAdapter() {
    // Layout Inflater
    private var mContext = ctx
    private var mImageList = imagesList
    private var mLayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        // return the number of images
        return imagesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.clicked_image_item, container, false)
        val imageView = itemView.findViewById<View>(R.id.clickedImageView) as ImageView
        imageView.setImageBitmap(imagesList[position])
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}