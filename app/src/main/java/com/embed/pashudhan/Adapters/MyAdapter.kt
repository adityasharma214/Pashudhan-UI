package com.embed.pashudhan.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.embed.pashudhan.DataModels.Animals
import com.embed.pashudhan.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyAdapter(private val animallist: ArrayList<Animals>, private val mContext: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemview =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pashubazar_card_item, parent, false)
        return MyViewHolder(itemview)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val animal: Animals = animallist[position]
        val storage = Firebase.storage
//        var storageRef = storage.reference

//        val gsReference = storage.getReferenceFromUrl(animal.img1.toString())
        holder.animal.text = animal.animal
        holder.price.text = animal.price
        Glide.with(mContext).load(animal.img1).placeholder(R.drawable.download).into(holder.img1)

    }

    override fun getItemCount(): Int {
      return animallist.size
    }

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img1: ImageView = itemview.findViewById(R.id.imageView9)
        val animal: TextView = itemview.findViewById(R.id.textView15)
        val price: TextView = itemview.findViewById(R.id.textView14)
    }

}