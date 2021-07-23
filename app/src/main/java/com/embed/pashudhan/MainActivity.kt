package com.embed.pashudhan

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.credentials.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    private lateinit var root: LinearLayout
    private lateinit var phone_number: EditText
    private lateinit var send_button: Button
    private lateinit var verify_otp_btn: Button
    private lateinit var otp_number: String
    private lateinit var resend_otp_btn: TextView
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedPhonenumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START initialize_auth]
        // Initialize Firebase Auth

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = "0"
        val mMobileUuid = sharedPref.getString("mobile_uuid", defaultValue)
        Log.d("MainActivity==>", mMobileUuid.toString())
        if(mMobileUuid != "0") {
            val intent = Intent(this, Pashubazar::class.java)
            intent.putExtra("uid", mMobileUuid)
            startActivity(intent)
        }

        auth = Firebase.auth
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                val currentUser = auth.currentUser
                callverifyotpUI(currentUser)

            }
        }
        // [END phone_auth_callbacks]

    }

    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Then, pass the HintRequest object to
        // credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the user to
        // choose a phone number.
        val credentialsClient = Credentials.getClient(applicationContext, options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, Bundle()
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val currentuser = FirebaseAuth.getInstance().currentUser
//                        Log.d(TAG, "Current User ID: $currentUser")

                    val user = task.result?.user

                    Log.d(TAG, "Current User ID: ${currentuser?.uid}")

                    val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putString("mobile_uuid", phone_number.text.toString())
                        apply()
                    }

                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Registration_page::class.java)
                    intent.putExtra("uid", phone_number.text.toString())
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    // [START on_start_check_user]
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
        phone_number = findViewById(R.id.editTextPhone2)
        send_button = findViewById(R.id.button)
        root = findViewById(R.id.main_activity_root_layout)

        phone_number.setOnClickListener {
            if (phone_number.text.toString() == "") phoneSelection()
        }

        send_button.setOnClickListener {
            if (phone_number.text.toString() == "") {
                var mSnackbar =
                    Snackbar.make(this, root, "कृप्या फ़ोन नंबर डाले", Snackbar.LENGTH_SHORT)
                mSnackbar.setAction("नंबर डाले", View.OnClickListener {
                    phoneSelection()
                })
                mSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.accent2))
                mSnackbar.show()
            } else {
                Log.d("PhoneNumber: ", phone_number.text.toString())
                storedPhonenumber = phone_number.text.toString()
                startPhoneNumberVerification(phoneNumber = phone_number.text.toString())

            }
        }
    }

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {
        setContentView(R.layout.activity_main)
    }

    private fun callverifyotpUI(user: FirebaseUser? = auth.currentUser) {
        setContentView(R.layout.fill_otp_layout)

        otp1 = findViewById(R.id.otpVal1)
        otp2 = findViewById(R.id.otpVal2)
        otp3 = findViewById(R.id.otpVal3)
        otp4 = findViewById(R.id.otpVal4)
        otp5 = findViewById(R.id.otpVal5)
        otp6 = findViewById(R.id.otpVal6)





        verify_otp_btn = findViewById(R.id.verify_otp_btn)
        resend_otp_btn = findViewById(R.id.resend_btn)
        verify_otp_btn.setOnClickListener {
            otp_number =
                otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()
            Log.d("otp", otp_number)
            verifyPhoneNumberWithCode(verificationId = storedVerificationId, code = otp_number)

        }
        resend_otp_btn.setOnClickListener {
            resendVerificationCode(phoneNumber = storedPhonenumber, token = resendToken)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {

            // get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // set the received data t the text view
            credential?.apply {
                phone_number.setText(credential.id)
            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
        var CREDENTIAL_PICKER_REQUEST = 1
    }


}
