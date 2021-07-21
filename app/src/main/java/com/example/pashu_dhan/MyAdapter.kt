package com.example.pashu_dhan

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

class MyAdapter(private val animallist: ArrayList<animals>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return MyViewHolder(itemview)

    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
      val animal : animals = animallist[position]

        holder.animal.text  =  animal.animal
        holder.price.text = animal.price
        Log.d("==message", animal.price.toString())
        Log.d("==message", animal.animal.toString())

    }

    override fun getItemCount(): Int {
      return animallist.size
    }

    public class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
    {
        //val img1 : ImageView = itemview.findViewById(R.id.add_img1)
        val animal : TextView = itemview.findViewById(R.id.textView15)
        val price : TextView = itemview.findViewById(R.id.textView14)
    }

}