package com.embed.pashudhan.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.embed.pashudhan.Firebase
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R

class UserRegisterationActivity : AppCompatActivity() {

    companion object {
        private val TAG = "UserRegisterationActivity==>"
    }

    private val PashudhanDB = Firebase()

    private lateinit var mFirstNameEditText: EditText
    private lateinit var mLastNameEditText: EditText
    private lateinit var mPincodeEditText: EditText
    private lateinit var mVillageEditText: EditText
    private lateinit var mDistrictEditText: EditText
    private lateinit var mStateEditText: EditText
    private lateinit var mSubmitBtn: Button
    private lateinit var mUserUUID: String
    private lateinit var userRegistrationRootLayout: LinearLayout

    // Get Access to Helper Functions
    private var helper: Helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_registeration_activity_layout)

        userRegistrationRootLayout = findViewById(R.id.user_registration_root_layout)

        mUserUUID = intent.getStringExtra(getString(R.string.sp_loginUserUUID)).toString()

        mFirstNameEditText = findViewById(R.id.firstNameEditText)
        mLastNameEditText = findViewById(R.id.lastNameEditText)
        mPincodeEditText = findViewById(R.id.pinCodeEditText)
        mVillageEditText = findViewById(R.id.villageEditText)
        mDistrictEditText = findViewById(R.id.districtEditText)
        mStateEditText = findViewById(R.id.stateEditText)
        mSubmitBtn = findViewById(R.id.userRegistration_submitButton)
        mSubmitBtn.setOnClickListener {
            uploadDataToFirestore()
        }
    }

    private fun uploadDataToFirestore() {

        val firstNameVal = mFirstNameEditText.text.toString()
        val lastNameVal = mLastNameEditText.text.toString()
        val pinCodeVal = mPincodeEditText.text.toString()
        val villageVal = mVillageEditText.text.toString()
        val districtVal = mDistrictEditText.text.toString()
        val stateVal = mStateEditText.text.toString()

        var userRegistrationData = hashMapOf(
            "firstName" to firstNameVal,
            "lastName" to lastNameVal,
            "pinCode" to pinCodeVal,
            "village" to villageVal,
            "district" to districtVal,
            "state" to stateVal
        )

        PashudhanDB.addDocument(mUserUUID, "users", userRegistrationData)
            .addOnSuccessListener {
                helper.showSnackbar(
                    this,
                    userRegistrationRootLayout,
                    getString(R.string.userRegistrationActivity_dataUploadSuccessMessage),
                    helper.SUCCESS_STATE
                )

                var handler = Handler()

                handler.postDelayed({
                    val intent = Intent(this, Pashubazar::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)

            }
            .addOnFailureListener { e ->
                helper.showSnackbar(
                    this,
                    userRegistrationRootLayout,
                    getString(R.string.tryAgainMessage),
                    helper.ERROR_STATE
                )
            }
    }


}