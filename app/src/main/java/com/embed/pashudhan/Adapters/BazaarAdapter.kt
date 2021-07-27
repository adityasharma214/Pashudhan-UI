package com.embed.pashudhan.Adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.Pashubazaar
import com.embed.pashudhan.R

class BazaarAdapter(
    private val mAnimalList: ArrayList<Pashubazaar>,
    private val mContext: Context
) :
    RecyclerView.Adapter<BazaarAdapter.MyViewHolder>() {

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
        val imageUri = Uri.parse(animalData.animalImages?.get(1))
        Log.d(TAG, imageUri.toString())
        Glide.with(mContext).load(imageUri).placeholder(R.drawable.download)
            .into(holder.animalImages)
        holder.animalDistance.text = "${animalData.location?.get(1)}"

    }

    override fun getItemCount(): Int {
        return mAnimalList.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val animalImages: ImageView = itemview.findViewById(R.id.pashuBazaar_cardImageView)
        val animalTypeBreed: TextView = itemview.findViewById(R.id.pashuBazaar_cardHeading)
        val animalPrice: TextView = itemview.findViewById(R.id.pashuBazaar_cardPrice)
        val animalDistance: TextView = itemview.findViewById(R.id.pashuBazaar_cardDistance)
    }

}