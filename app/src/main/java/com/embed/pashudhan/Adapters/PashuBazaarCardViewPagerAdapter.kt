package com.embed.pashudhan.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.R
import java.util.*

class PashuBazaarCardViewPagerAdapter(holder: BazaarAdapter.MyViewHolder, images: ArrayList<Uri>) :
    RecyclerView.Adapter<PashuBazaarCardViewPagerAdapter.PashuBazaarCardImageViewHolder>() {

    private var mContext = holder.itemView.context
    private var mParent = holder
    private var mImageList = images


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PashuBazaarCardImageViewHolder {
        var view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pashubazaar_card_viewpager_item, parent, false)
        return PashuBazaarCardImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PashuBazaarCardImageViewHolder, position: Int) {
        var image = mImageList[position]
        Glide.with(mContext).load(image).placeholder(R.drawable.download)
            .into(holder.pashuBazaarCardImage)
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    class PashuBazaarCardImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pashuBazaarCardImage: ImageView =
            itemView.findViewById(R.id.pashubazaar_card_viewPager_itemImage)
    }

}