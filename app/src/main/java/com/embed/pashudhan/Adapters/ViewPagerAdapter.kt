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
import kotlin.collections.ArrayList


internal class ViewPagerAdapter(// Context object
    var context: Context, // Array of images
    var images: ArrayList<Int>,
    var images_bitmap: ArrayList<Bitmap>
) :
    PagerAdapter() {
    // Layout Inflater
    var mLayoutInflater: LayoutInflater
    override fun getCount(): Int {
        // return the number of images
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // inflating the item.xml
        val itemView = mLayoutInflater.inflate(R.layout.item, container, false)

        // referencing the image view from the item.xml file
        val imageView = itemView.findViewById<View>(R.id.imageViewMain) as ImageView

        // setting the image in the imageView
        imageView.setImageBitmap(images_bitmap[position])

        // Adding the View
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    // Viewpager Constructor
    init {
        images = images
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}