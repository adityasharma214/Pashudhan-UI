package com.embed.pashudhan.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.embed.pashudhan.DataModels.Pashubazaar
import com.embed.pashudhan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BazaarAdapter(
    private val mAnimalList: ArrayList<Pashubazaar>,
    private val mContext: Context
) :
    RecyclerView.Adapter<BazaarAdapter.MyViewHolder>() {

    private lateinit var mViewPagerAdapter: PashuBazaarCardViewPagerAdapter

    companion object {
        private val TAG = "BazaarAdapter==>"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemview =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pashubazar_card_item, parent, false)
        return MyViewHolder(itemview)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        fun getItem(i: Int): Int {
            return holder.animalImages.currentItem + i
        }

        val animalData: Pashubazaar = mAnimalList[position]
        holder.animalTypeBreed.text = "${animalData.animalType}, ${animalData.animalBreed}"
        holder.animalPrice.text = "${animalData.animalPrice}"

        var imageUriList = arrayListOf<Uri>()
        for (item in animalData.animalImages!!) {
            imageUriList.add(Uri.parse(item))
        }
        mViewPagerAdapter = PashuBazaarCardViewPagerAdapter(holder, imageUriList)
        holder.animalImages.isUserInputEnabled = false
        holder.animalImages.adapter = mViewPagerAdapter
        holder.nextImageButton.setOnClickListener {
            holder.animalImages.setCurrentItem(getItem(+1), true)
        }
        holder.prevImageButton.setOnClickListener {
            holder.animalImages.setCurrentItem(getItem(-1), true)
        }

    }

    override fun getItemCount(): Int {
        return mAnimalList.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val animalImages: ViewPager2 = itemview.findViewById(R.id.pashuBazaar_cardViewPager)
        val animalTypeBreed: TextView = itemview.findViewById(R.id.pashuBazaar_cardHeading)
        val animalPrice: TextView = itemview.findViewById(R.id.pashuBazaar_cardPrice)
        val animalDistance: TextView = itemview.findViewById(R.id.pashuBazaar_cardDistance)
        val nextImageButton: FloatingActionButton =
            itemview.findViewById(R.id.pashubazaar_cardNextImageButton)
        val prevImageButton: FloatingActionButton =
            itemview.findViewById(R.id.pashubazaar_cardPrevImageButton)
    }

}