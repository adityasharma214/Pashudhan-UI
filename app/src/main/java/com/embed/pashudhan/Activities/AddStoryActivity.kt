package com.embed.pashudhan.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.embed.pashudhan.Helper
import com.embed.pashudhan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.add_story_activity_layout.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class AddStoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXBasic==>"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mCameraCaptureBtn: FloatingActionButton
    private lateinit var mUserUUID: String
    private lateinit var mClickedImageView: ImageView
    private var helper = Helper()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_story_activity_layout)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        val checkLoginSharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID =
            checkLoginSharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()
        mCameraCaptureBtn = findViewById(R.id.cameraCaptureBtn)
        // Set up the listener for take photo button
        mCameraCaptureBtn.setOnClickListener {
            Log.d(TAG, "CLICKED")
            takePhoto()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        Log.d(TAG, "HI")
        val imageCapture = imageCapture ?: return
        val fileName = helper.getRandomString(15)
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory, "${fileName}_${System.currentTimeMillis() / 1000}.jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, savedUri.toString())
                    val intent = Intent(this@AddStoryActivity, ViewStoryActivity::class.java)
                    intent.putExtra("imageUri", savedUri.toString())
                    startActivity(intent)
                    finish()
                }
            })
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        bindCameraLifeCycle()

        var switchCameraButton = findViewById<ImageButton>(R.id.switchCameraBtn)
        switchCameraButton.setOnClickListener {

            if (cameraSelector.lensFacing == CameraSelector.LENS_FACING_BACK) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }
            try {
                bindCameraLifeCycle()
            } catch (exc: Exception) {
            }
        }

        var closeCameraButton = findViewById<ImageButton>(R.id.closeCameraBtn)
        closeCameraButton.setOnClickListener {
            val intent = Intent(this, PashuStoryActivity::class.java)
            startActivity(intent)
            cameraExecutor.shutdown()
            finish()
        }

        var flashButton = findViewById<ImageButton>(R.id.flashBtn)
        flashButton.setOnClickListener {
            when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode = ImageCapture.FLASH_MODE_ON
                    flashButton.setImageDrawable(getDrawable(R.drawable.ic_flash_on))
                }
                ImageCapture.FLASH_MODE_ON -> {
                    flashMode = ImageCapture.FLASH_MODE_AUTO
                    flashButton.setImageDrawable(getDrawable(R.drawable.ic_flash_auto))
                }
                ImageCapture.FLASH_MODE_AUTO -> {
                    flashMode = ImageCapture.FLASH_MODE_OFF
                    flashButton.setImageDrawable(getDrawable(R.drawable.ic_flash_off))
                }
            }
            // Re-bind use cases to include changes
            bindCameraLifeCycle()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraLifeCycle() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(storyViewport.surfaceProvider)
                }
            val viewPort = findViewById<PreviewView>(R.id.storyViewport).viewPort
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setFlashMode(flashMode)
                .build()
            // Select back camera as a default

            val useCaseGroup = viewPort?.let {
                UseCaseGroup.Builder()
                    .addUseCase(preview)
                    .addUseCase(imageCapture!!)
                    .setViewPort(it)
                    .build()
            }
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCaseGroup!!
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

}