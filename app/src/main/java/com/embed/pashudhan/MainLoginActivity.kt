package com.embed.pashudhan

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class MainLoginActivity : AppCompatActivity() {

    // Constants
    companion object {
        private const val TAG = "MainLoginActivity==>"
        var CREDENTIAL_PICKER_REQUEST = 1
    }

    // Declare Variables

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var rootLayout: LinearLayout
    private lateinit var otpLayoutView: LinearLayout
    private lateinit var mPhoneNumberEditText: EditText
    private lateinit var mSubmitBtn: Button
    private lateinit var mVerifyOTPBtn: Button
    private lateinit var mOTPNumberVal: String
    private lateinit var mResendOTPTextView: TextView
    private var mVerificationIDVal: String? = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mPhoneAuthCallbackFunctions: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mPhoneNumberVal: String
    private var mOtherAccount = 0
    private lateinit var mOTPBuilder: StringBuilder

    // Get Access to Helper Functions
    private var helper: Helper = Helper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get Shared Preferences to check if user is already logged in.
        val checkLoginSharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val mUserUUID = checkLoginSharedPref.getString(getString(R.string.loginUserUUID), "0")

        // If user is already logged in => redirect to Pashubaazar::class.java
        if (mUserUUID != "0") {
            val intent = Intent(this, Pashubazar::class.java)
            intent.putExtra(getString(R.string.loginUserUUID), mUserUUID)
            startActivity(intent)
            finish()

        } else {

            mFirebaseAuth = Firebase.auth

            val currentUser = mFirebaseAuth.currentUser
            updateUI(currentUser)

            mPhoneNumberEditText = findViewById(R.id.editTextPhone2)
            mSubmitBtn = findViewById(R.id.button)
            rootLayout = findViewById(R.id.main_activity_root_layout)

            // Get Saved Phone Numbers to prevent users from manual editing
            mPhoneNumberEditText.setOnClickListener {
                if (mPhoneNumberEditText.text.toString() == "" && mOtherAccount == 0) phoneSelection()
            }

            mSubmitBtn.setOnClickListener {
                if (mPhoneNumberEditText.text.toString() == "") {
                    helper.showSnackbar(
                        this,
                        rootLayout,
                        "कृप्या फ़ोन नंबर डाले",
                        helper.ERROR_STATE,
                        "नंबर डाले",
                        this::phoneSelection,
                        R.color.accent2
                    )
                } else {
                    mPhoneNumberVal = mPhoneNumberEditText.text.toString()
                    startPhoneNumberVerification(phoneNumber = mPhoneNumberVal)

                }
            }

            mPhoneAuthCallbackFunctions =
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    // For Instant Verification, and Auto-Retrieval
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    // For Invalid Request (Phone Number not valid)
                    override fun onVerificationFailed(e: FirebaseException) {

                        var message: String = ""

                        if (e is FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            message = "कृपया दुबारा कोशिश करें"
                        } else if (e is FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            message = "कुछ समय बाद दुबारा प्रयास करें"
                        }

                        // Show a message and update the UI
                        helper.showSnackbar(
                            this@MainLoginActivity,
                            rootLayout,
                            message,
                            helper.ERROR_STATE
                        )
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Save verification ID and resending token so we can use them later
                        mVerificationIDVal = verificationId
                        mResendToken = token
                        val currentUser = mFirebaseAuth.currentUser
                        callVerifyOTPUI(currentUser)
                    }
                }
        }
    }

    /*
    *  Helper Functions
    *   To provide supported UI functionality for different cases.
    *
    * */

    // To retrieve the Phone Number hints,
    private fun setOTPFillingUX() {
        mOTPBuilder = StringBuilder()

        var mOTPEditText1 = findViewById<EditText>(R.id.otpVal1)
        var mOTPEditText2 = findViewById<EditText>(R.id.otpVal2)
        var mOTPEditText3 = findViewById<EditText>(R.id.otpVal3)
        var mOTPEditText4 = findViewById<EditText>(R.id.otpVal4)
        var mOTPEditText5 = findViewById<EditText>(R.id.otpVal5)
        var mOTPEditText6 = findViewById<EditText>(R.id.otpVal6)


        mOTPEditText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 0) mOTPEditText1.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 1) mOTPBuilder.deleteCharAt(0)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 0 && mOTPEditText1.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText1.clearFocus()
                    mOTPEditText2.requestFocus()
                    mOTPEditText2.isCursorVisible = true
                }
            }
        })

        mOTPEditText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 1) mOTPEditText2.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 2) mOTPBuilder.deleteCharAt(1)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 1 && mOTPEditText2.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText2.clearFocus()
                    mOTPEditText3.requestFocus()
                    mOTPEditText3.isCursorVisible = true
                }
            }
        })

        mOTPEditText3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 2) mOTPEditText3.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 3) mOTPBuilder.deleteCharAt(2)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 2 && mOTPEditText3.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText3.clearFocus()
                    mOTPEditText4.requestFocus()
                    mOTPEditText4.isCursorVisible = true
                }
            }
        })

        mOTPEditText4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 3) mOTPEditText4.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 4) mOTPBuilder.deleteCharAt(3)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 3 && mOTPEditText4.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText4.clearFocus()
                    mOTPEditText5.requestFocus()
                    mOTPEditText5.isCursorVisible = true
                }
            }
        })

        mOTPEditText5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 4) mOTPEditText5.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 5) mOTPBuilder.deleteCharAt(4)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 4 && mOTPEditText5.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText5.clearFocus()
                    mOTPEditText6.requestFocus()
                    mOTPEditText6.isCursorVisible = true
                }
            }
        })

        mOTPEditText6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mOTPBuilder.length == 5) mOTPEditText6.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (mOTPBuilder.length == 6) mOTPBuilder.deleteCharAt(5)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mOTPBuilder.length == 5 && mOTPEditText5.text.length == 1) {
                    mOTPBuilder.append(s)
                    mOTPEditText6.clearFocus()
                }
            }
        })
    }


    private fun phoneSelection() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Create intent to prompt user to select number
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "data" + data.toString())
            // get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // Set the phone number
            credential?.apply {
                mPhoneNumberEditText.setText(credential.id)
            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            helper.showSnackbar(
                this,
                rootLayout,
                "कोई फ़ोन नंबर प्राप्त नहीं हुए",
                helper.ERROR_STATE
            )
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_OTHER_ACCOUNT) {
            mOtherAccount = 1
        }
    }


    /*
    *  Firebase AUTH
    *
    * */

    private fun startPhoneNumberVerification(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mPhoneAuthCallbackFunctions)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mPhoneAuthCallbackFunctions)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        otpLayoutView = findViewById<LinearLayout>(R.id.fillOTPLayout)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user

                    var mOTPDigits = credential.smsCode?.split("")
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(0))
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(1))
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(2))
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(3))
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(4))
                    Log.d(TAG, "OTP Digits" + mOTPDigits?.get(5))
                    findViewById<EditText>(R.id.otpVal1).setText(mOTPDigits?.get(0))
                    findViewById<EditText>(R.id.otpVal2).setText(mOTPDigits?.get(1))
                    findViewById<EditText>(R.id.otpVal3).setText(mOTPDigits?.get(2))
                    findViewById<EditText>(R.id.otpVal4).setText(mOTPDigits?.get(3))
                    findViewById<EditText>(R.id.otpVal5).setText(mOTPDigits?.get(4))
                    findViewById<EditText>(R.id.otpVal6).setText(mOTPDigits?.get(5))

                    val setLoginSharedPref =
                        this@MainLoginActivity.getPreferences(Context.MODE_PRIVATE)
                    with(setLoginSharedPref.edit()) {
                        putString(getString(R.string.loginUserUUID), mPhoneNumberVal)
                        putString(getString(R.string.userInfo), user.toString())
                        apply()
                    }

                    helper.showSnackbar(this, otpLayoutView, "प्रवेश सफल", helper.SUCCESS_STATE)

                    var handler = Handler()

                    handler.postDelayed({
                        val intent = Intent(this, Registration_page::class.java)
                        intent.putExtra(getString(R.string.loginUserUUID), mPhoneNumberVal)
                        startActivity(intent)
                        finish()
                    }, 1000)

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        helper.showSnackbar(
                            this,
                            otpLayoutView,
                            "कृप्या सही ओटीपी डाले",
                            helper.ERROR_STATE
                        )
                    }
                    // Update UI
                    helper.showSnackbar(
                        this,
                        otpLayoutView,
                        "कृप्या दुबारा प्रयास करें",
                        helper.ERROR_STATE
                    )
                }
            }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//
//    }


    private fun updateUI(user: FirebaseUser? = mFirebaseAuth.currentUser) {
        Log.d(TAG, user.toString())
        setContentView(R.layout.main_login_activity_layout)
    }

    private fun callVerifyOTPUI(user: FirebaseUser? = mFirebaseAuth.currentUser) {
        setContentView(R.layout.fill_otp_layout)
        otpLayoutView = findViewById<LinearLayout>(R.id.fillOTPLayout)
        setOTPFillingUX()

        val mOTPDigitsArray = arrayOfNulls<String>(6)

//        mOTPDigitsArray.set(0, findViewById<EditText>(R.id.otpVal1).text.toString());
//        mOTPDigitsArray.set(1, findViewById<EditText>(R.id.otpVal2).text.toString());
//        mOTPDigitsArray.set(2, findViewById<EditText>(R.id.otpVal3).text.toString());
//        mOTPDigitsArray.set(3, findViewById<EditText>(R.id.otpVal4).text.toString());
//        mOTPDigitsArray.set(4, findViewById<EditText>(R.id.otpVal5).text.toString());
//        mOTPDigitsArray.set(5, findViewById<EditText>(R.id.otpVal6).text.toString());


        mVerifyOTPBtn = findViewById(R.id.verify_otp_btn)
        mResendOTPTextView = findViewById(R.id.resend_btn)

        mVerifyOTPBtn.setOnClickListener {
            if (mOTPBuilder.length < 6) {
                helper.showSnackbar(this, otpLayoutView, "कृपया पूरा ओटीपी भरे", helper.ERROR_STATE)
            } else if (mOTPBuilder.length == 6) {
                mOTPNumberVal = mOTPBuilder.toString()
                verifyPhoneNumberWithCode(verificationId = mVerificationIDVal, code = mOTPNumberVal)
            }

        }
        mResendOTPTextView.setOnClickListener {
            resendVerificationCode(phoneNumber = mPhoneNumberVal, token = mResendToken)
        }

    }


}
