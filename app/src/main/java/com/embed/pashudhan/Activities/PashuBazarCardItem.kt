package com.embed.pashudhan.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.embed.pashudhan.R


class PashuBazarCardItem : AppCompatActivity() {


    lateinit var price : TextView
    lateinit var breed : TextView
    lateinit var milkCapacity : TextView
    lateinit var milkQuantity : TextView
    lateinit var animalType: TextView
    lateinit var Byaat : TextView
    lateinit var animalAge : TextView
    lateinit var animalDetails : TextView
    lateinit var priceTag : TextView
    lateinit var User_id : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_pashu_bazar_card_item)

        breed = findViewById(R.id.breed)
        price = findViewById(R.id.price)
        milkCapacity = findViewById(R.id.milkCapacity)
        milkQuantity = findViewById(R.id.milkQuantity)
        animalType = findViewById(R.id.animalType)
        Byaat = findViewById(R.id.animalByaat)
        animalAge = findViewById(R.id.animalAge)
        animalDetails = findViewById(R.id.textView21)
        priceTag = findViewById(R.id.textView23)
        User_id = findViewById(R.id.user_phoneNo)

        breed.text = getIntent().getStringExtra("Breed")
        price.text = getIntent().getStringExtra("Price")
        milkCapacity.text = getIntent().getStringExtra("MilkCapacity")
        animalDetails.text = getIntent().getStringExtra("Type")
        milkQuantity.text = getIntent().getStringExtra("MilkQuantity")
        animalType.text = getIntent().getStringExtra("Type")
        Byaat.text = getIntent().getStringExtra("Byaat")
        animalAge.text = getIntent().getStringExtra("Age")
        priceTag.text = getIntent().getStringExtra("Price")
        User_id.text = getIntent().getStringExtra("user_uuid")

    }
}