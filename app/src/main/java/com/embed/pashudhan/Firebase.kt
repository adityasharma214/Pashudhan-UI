package com.embed.pashudhan

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.net.URI

class Firebase : AppCompatActivity() {

    private val mFirebaseDB = Firebase.firestore
    private val mFirebaseStorage = Firebase.storage

    fun getCollection(collectionName: String): Task<QuerySnapshot> {
        return mFirebaseDB.collection(collectionName)
            .get()
    }

    fun getDocument(documentID: String, collectionName: String): Task<DocumentSnapshot> {
        return mFirebaseDB.collection(collectionName)
            .document(documentID)
            .get()
    }

    fun addDocument(
        documentID: String,
        collectionName: String,
        document: Map<String, Any>
    ): Task<Void> {
        return mFirebaseDB.collection(collectionName)
            .document(documentID)
            .set(document)
    }

    fun addDocument(collectionName: String, document: Map<String, Any>): Task<DocumentReference> {
        return mFirebaseDB.collection(collectionName)
            .add(document)
    }

    fun addDocuments(collectionName: String, documents: Array<Map<String, Any>>) {}

    fun uploadFile(file: URI, basePath: String) {}

    fun uploadFiles(files: ArrayList<Uri>, basePath: String): ArrayList<Uri>? {

        var storageRef = mFirebaseStorage.reference
        var downloadUriList = ArrayList<Uri>()

        files.forEach { file ->
            var childRef: StorageReference? = storageRef.child("${basePath}${file.lastPathSegment}")
            var uploadTask = childRef?.putFile(file)
            childRef?.downloadUrl?.addOnSuccessListener(OnSuccessListener<Uri?> {
                //do something with downloadurl
                downloadUriList.add(it)
            })
        }

        if (downloadUriList.size == files.size) return downloadUriList
        return null
    }


}