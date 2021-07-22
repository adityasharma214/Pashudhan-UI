package com.example.pashu_dhan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class Pashubazar : AppCompatActivity() {

    private lateinit var submit_btn : Button

    private lateinit var recyclerview : RecyclerView
    private lateinit var animalarraylist : ArrayList<animals>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pashubazar)

        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        recyclerview.setHasFixedSize(true)
        animalarraylist = arrayListOf()

        myAdapter = MyAdapter(animalarraylist, this@Pashubazar)
        recyclerview.adapter = myAdapter



        submit_btn = findViewById(R.id.button4)
        submit_btn.setOnClickListener {

            val intent = Intent(this, Pashusell::class.java)
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
                        animalarraylist.add(dc.document.toObject(animals::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })

    }
}