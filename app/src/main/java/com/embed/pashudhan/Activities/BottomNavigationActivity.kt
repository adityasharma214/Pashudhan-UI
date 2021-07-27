package com.embed.pashudhan.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.embed.pashudhan.Fragments.PashuBazaarFragment
import com.embed.pashudhan.Fragments.PashuSamwardhanFragment
import com.embed.pashudhan.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationActivity : AppCompatActivity() {

    private val pashuBazarFragment = PashuBazaarFragment()
    private val pashuSamwardhanFragment = PashuSamwardhanFragment()
    private lateinit var mBottomNavigationMenu: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.botton_navigation_activity_layout)
        var selectedFragment = intent.getStringExtra("fragment").toString()
        if (selectedFragment == "pashuBazaar") {
            replaceFragment(pashuBazarFragment)
        } else if (selectedFragment == "pashuSamwardhan") {
            replaceFragment(pashuSamwardhanFragment)
        } else {
            replaceFragment(pashuBazarFragment)
        }

        mBottomNavigationMenu = findViewById(R.id.bottom_navigation)
        mBottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuItem_pashuBazaar -> replaceFragment(pashuBazarFragment)
                R.id.menuItem_pashuSamwardhan -> replaceFragment(pashuSamwardhanFragment)
                R.id.menuItem_pashuStory -> {
                    val intent = Intent(this, PashuStoryActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_frameLayout, fragment)
            transaction.commit()
        }
    }

}