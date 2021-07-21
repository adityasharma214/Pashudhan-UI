package com.example.pashu_dhan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Pashubazar : AppCompatActivity() {

    private lateinit var submit_btn : Button

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pashubazar)

        submit_btn = findViewById(R.id.button4)
        submit_btn.setOnClickListener {

            val intent = Intent(this, Pashusell::class.java)
            startActivity(intent)

        }
    }
}