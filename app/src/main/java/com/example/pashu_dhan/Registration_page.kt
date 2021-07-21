package com.example.pashu_dhan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registration_page : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var pincode: EditText
    private lateinit var dist: EditText
    private lateinit var usr_state : EditText
    private lateinit var village : EditText
    private lateinit var submit_btn : Button
    private lateinit var user_uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_page)

        val usr_id = intent.getStringExtra("uid")
        Log.d("usr_id", usr_id.toString())
        user_uid = usr_id.toString()
        fname = findViewById(R.id.editTextTextPersonName2)
        lname = findViewById(R.id.editTextTextPersonName4)
        pincode = findViewById(R.id.editTextTextPersonName5)
        dist = findViewById(R.id.editTextTextPersonName)
        usr_state = findViewById(R.id.editTextTextPersonName3)
        village = findViewById(R.id.editTextTextPersonName6)
        submit_btn = findViewById(R.id.button2)
        submit_btn.setOnClickListener {
            upload_data()
            val intent = Intent(this, Pashubazar::class.java)
            startActivity(intent)

        }
    }

    private fun upload_data(){
        Log.d("fname", pincode.text.toString())
        val user = hashMapOf(
            "fname" to fname.text.toString(),
            "lname" to lname.text.toString(),
            "pin_code" to pincode.text.toString(),
            "state" to usr_state.text.toString(),
            "dist" to dist.text.toString(),
            "village" to village.text.toString()
        )
        db.collection("users")
            .document(user_uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $user_uid")
                val intent = Intent(this, Pashubazar::class.java)
                startActivity(intent)

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    companion object {
        private const val TAG = "FirebaseFireStore"
    }

}