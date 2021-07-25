package com.embed.pashudhan.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.embed.pashudhan.Firebase
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*


class UserRegisterationActivity : AppCompatActivity() {

    companion object {
        private val TAG = "UserRegisterationActivity==>"
        private val PERMISSION_ID = 42
    }

    private val PashudhanDB = Firebase()

    private lateinit var mFirstNameEditText: EditText
    private lateinit var mLastNameEditText: EditText
    private lateinit var mAddressEditText: EditText
    private lateinit var mPincodeEditText: EditText
    private lateinit var mVillageEditText: EditText
    private lateinit var mDistrictEditText: EditText
    private lateinit var mStateEditText: EditText
    private lateinit var mSubmitBtn: Button
    private lateinit var mUserUUID: String
    private lateinit var userRegistrationRootLayout: LinearLayout
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mAddressProgressBar: ProgressBar
    var currentLocation: LatLng = LatLng(20.5, 78.9)

    // Get Access to Helper Functions
    private var helper: Helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_registeration_activity_layout)

        userRegistrationRootLayout = findViewById(R.id.user_registration_root_layout)

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mUserUUID = intent.getStringExtra(getString(R.string.sp_loginUserUUID)).toString()

        mFirstNameEditText = findViewById(R.id.firstNameEditText)
        mLastNameEditText = findViewById(R.id.lastNameEditText)
        mAddressEditText = findViewById(R.id.addressEditText)
        mPincodeEditText = findViewById(R.id.pinCodeEditText)
        mVillageEditText = findViewById(R.id.villageEditText)
        mDistrictEditText = findViewById(R.id.districtEditText)
        mStateEditText = findViewById(R.id.stateEditText)
        mSubmitBtn = findViewById(R.id.userRegistration_submitButton)
        mAddressProgressBar = findViewById(R.id.addressLoadingBar)

        mAddressProgressBar.visibility = View.VISIBLE
        getLastLocation()

        mSubmitBtn.setOnClickListener {
            uploadDataToFirestore()
        }
    }

    private fun uploadDataToFirestore() {

        val firstNameVal = mFirstNameEditText.text.toString()
        val lastNameVal = mLastNameEditText.text.toString()
        val addressVal = mAddressEditText.text.toString()
        val pinCodeVal = mPincodeEditText.text.toString()
        val villageVal = mVillageEditText.text.toString()
        val districtVal = mDistrictEditText.text.toString()
        val stateVal = mStateEditText.text.toString()

        if (
            firstNameVal != "" &&
            lastNameVal != "" &&
            addressVal != "" &&
            pinCodeVal != "" &&
            villageVal != "" &&
            districtVal != "" &&
            stateVal != ""
        ) {
            var userRegistrationData = hashMapOf(
                "firstName" to firstNameVal,
                "lastName" to lastNameVal,
                "address" to addressVal,
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
                        getString(R.string.userRegistrationActivity_registrationSuccessMessage),
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
        } else {
            helper.showSnackbar(
                this,
                userRegistrationRootLayout,
                getString(R.string.userRegistrationActivity_incompleteFormErrorMessage),
                helper.ERROR_STATE
            )
        }


    }

    // Get current location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        getLocationData(location)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun getLocationData(location: Location) {
        var geoCoder: Geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>

        var latitude: Double = location.latitude
        var longitude: Double = location.longitude

        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            Log.d(TAG, "Addresses" + addresses)
            if (addresses != null && addresses.isNotEmpty()) {
                var address = addresses.get(0).getAddressLine(0)
                var city = addresses.get(0).locality
                var state = addresses.get(0).adminArea
                var postalCode = addresses.get(0).postalCode

                mAddressEditText.setText(address)
                mDistrictEditText.setText(city)
                mStateEditText.setText(state)
                mPincodeEditText.setText(postalCode)

                mAddressProgressBar.visibility = View.GONE
            } else {
                helper.showSnackbar(
                    this,
                    userRegistrationRootLayout,
                    getString(R.string.userRegistrationActivity_noLocationFoundMessage),
                    helper.ERROR_STATE
                )
                mAddressProgressBar.visibility = View.GONE
            }
        } catch (e: IOException) {
            helper.showSnackbar(
                this,
                userRegistrationRootLayout,
                getString(R.string.tryAgainMessage),
                helper.ERROR_STATE
            )
        }


    }

    // Get current location, if shifted
    // from previous location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }


}