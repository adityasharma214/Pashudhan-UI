package com.embed.pashudhan.Activities

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.embed.pashudhan.R
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView

class PhotoEditorActivity : AppCompatActivity() {

    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mPhotoEditorView: PhotoEditorView
    private lateinit var mImageURI: Uri
    private lateinit var mUserUUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_editor_activity_layout)
        mImageURI = Uri.parse(intent.getStringExtra("imageUri").toString())
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mUserUUID = sharedPref.getString(getString(R.string.sp_loginUserUUID), "0").toString()

        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mPhotoEditorView.source.setImageURI(mImageURI)


        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true)
            .build()

    }
}