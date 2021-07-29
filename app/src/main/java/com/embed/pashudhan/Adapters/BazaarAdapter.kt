package com.embed.pashudhan.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.embed.pashudhan.DataModels.Pashubazaar
import com.embed.pashudhan.R

class BazaarAdapter(
    private val mAnimalList: ArrayList<Pashubazaar>,
    private val mContext: Context
) :
    RecyclerView.Adapter<BazaarAdapter.MyViewHolder>() {

    private lateinit var mViewPager: ViewPager
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

        val animalData: Pashubazaar = mAnimalList[position]
        holder.animalTypeBreed.text = "${animalData.animalType}, ${animalData.animalBreed}"
        holder.animalPrice.text = "${animalData.animalPrice}"
//        val imageUri = Uri.parse(animalData.animalImages?.get(1))
//        Log.d(TAG, imageUri.toString())
//        Glide.with(mContext).load(imageUri).placeholder(R.drawable.download)
//            .into(holder.animalImages)
//        holder.animalDistance.text = "${animalData.location?.get(1)}"
        var imageUriList = arrayListOf<Uri>()
        for (item in animalData.animalImages!!) {
            imageUriList.add(Uri.parse(item))
        }
        mViewPagerAdapter = PashuBazaarCardViewPagerAdapter(holder.itemView.context, imageUriList)
        holder.animalImages.adapter = mViewPagerAdapter
    }

    override fun getItemCount(): Int {
        return mAnimalList.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val animalImages: ViewPager = itemview.findViewById(R.id.pashuBazaar_cardViewPager)
        val animalTypeBreed: TextView = itemview.findViewById(R.id.pashuBazaar_cardHeading)
        val animalPrice: TextView = itemview.findViewById(R.id.pashuBazaar_cardPrice)
        val animalDistance: TextView = itemview.findViewById(R.id.pashuBazaar_cardDistance)
    }

}