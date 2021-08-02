package com.embed.pashudhan.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.embed.pashudhan.Adapters.BazaarAdapter
import com.embed.pashudhan.Adapters.OnBazaarItemClickListner
import com.embed.pashudhan.DataModels.Pashubazaar
import com.embed.pashudhan.R
import com.google.firebase.firestore.*

class Pashubazar : AppCompatActivity(), OnBazaarItemClickListner{

    private lateinit var submit_btn : Button

    private lateinit var recyclerview: RecyclerView
    private lateinit var animalarraylist: ArrayList<Pashubazaar>
    private lateinit var mBazaarAdapter: BazaarAdapter
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pashubazar)

        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        recyclerview.setHasFixedSize(true)
        animalarraylist = arrayListOf()

        mBazaarAdapter = BazaarAdapter(animalarraylist, this, this)
        recyclerview.adapter = mBazaarAdapter



        submit_btn = findViewById(R.id.button4)
        submit_btn.setOnClickListener {

            val intent = Intent(this, PashuSalesActivity::class.java)
            startActivity(intent)

        }
        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("animal-details").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        animalarraylist.add(dc.document.toObject(Pashubazaar::class.java))
                    }
                }
                mBazaarAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onItemclick(item: Pashubazaar, position: Int) {
        Toast.makeText(this, item.animalBreed, Toast.LENGTH_SHORT).show()
    }
}