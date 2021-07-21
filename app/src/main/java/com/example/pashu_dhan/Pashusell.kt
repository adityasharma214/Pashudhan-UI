package com.example.pashu_dhan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class Pashusell : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var imageView3: ImageView
    lateinit var imageView4: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var user_uid: String
    private lateinit var animal: String
    private lateinit var breed: String
    private lateinit var age: String
    private lateinit var byaat: String
    private lateinit var milk_quantity: String
    private lateinit var milk_capacity: String
    private lateinit var price : String

    var imagelist = ArrayList<Uri>()
    var selected_img_order = ArrayList<ImageView>()
    var uploaded_images = ArrayList<String>()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashusell)

        storage = Firebase.storage

        val usr_id = intent.getStringExtra("uid")
        Log.d("usr_id", usr_id.toString())
        user_uid = usr_id.toString()

//        animal list spinner adapter
        val animal_list = resources.getStringArray(R.array.Animals)
        val which_animal_spinner = findViewById<Spinner>(R.id.spinner_which_animal)
        if (which_animal_spinner != null) {
            val adapter = ArrayAdapter(this, R.layout.spinner_item, animal_list)
            which_animal_spinner.adapter = adapter
            which_animal_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + animal_list[position], Toast.LENGTH_SHORT).show()
                    animal = animal_list[position]
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
//        breed type spinner adapter
        val breed_list = resources.getStringArray(R.array.Breed)
        val breed_spinner = findViewById<Spinner>(R.id.breed_type)
        if (breed_spinner != null) {
            val breed_adapter = ArrayAdapter(this, R.layout.spinner_item, breed_list)
            breed_spinner.adapter = breed_adapter
            breed_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + breed_list[position], Toast.LENGTH_SHORT).show()
                    breed = breed_list[position]
                    breed_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
//        byaat spinner adapter
        val byaat_list = resources.getStringArray(R.array.Byaat)
        val byaat_spinner = findViewById<Spinner>(R.id.byaat)
        if (byaat_spinner != null) {
            val byaat_adapter = ArrayAdapter(this, R.layout.spinner_item, byaat_list)
            byaat_spinner.adapter = byaat_adapter
            byaat_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + byaat_list[position], Toast.LENGTH_SHORT).show()
                    byaat = byaat_list[position]
                    byaat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

//        abhi ka doodh spinner adapter
        val milk_quantity_list = resources.getStringArray(R.array.Milk_quantity)
        val milk_quantity_spinner = findViewById<Spinner>(R.id.milk_quantity)
        if (milk_quantity_spinner != null) {
            val milk_quantity_adapter = ArrayAdapter(this, R.layout.spinner_item, milk_quantity_list)
            milk_quantity_spinner.adapter = milk_quantity_adapter
            milk_quantity_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + milk_quantity_list[position], Toast.LENGTH_SHORT).show()
                    milk_quantity = milk_quantity_list[position]
                    milk_quantity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
//        doodh ki shamta
        val milk_capacity_list = resources.getStringArray(R.array.Milk_capacity)
        val milk_capacity_spinner = findViewById<Spinner>(R.id.milk_capacity)
        if (milk_capacity_spinner != null) {
            val milk_capacity_adapter = ArrayAdapter(this, R.layout.spinner_item, milk_capacity_list)
            milk_capacity_spinner.adapter = milk_capacity_adapter
            milk_capacity_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + milk_capacity_list[position], Toast.LENGTH_SHORT).show()
                    milk_capacity = milk_capacity_list[position]
                    milk_capacity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val open_image_delecter1 = findViewById<Button>(R.id.addphoto1)
        val open_image_delecter2 = findViewById<Button>(R.id.addphoto2)
        val open_image_delecter3 = findViewById<Button>(R.id.addphoto3)
        val open_image_delecter4 = findViewById<Button>(R.id.addphoto4)

        open_image_delecter1.setOnClickListener {
            setupPermissions()
            imageView1 = findViewById(R.id.imageView)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            selected_img_order.add(imageView1)


        }
        open_image_delecter2.setOnClickListener {
            setupPermissions()
            imageView2 = findViewById(R.id.imageView2)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            selected_img_order.add(imageView2)

        }
        open_image_delecter3.setOnClickListener {
            setupPermissions()
            imageView3 = findViewById(R.id.imageView15)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            selected_img_order.add(imageView3)

        }
        open_image_delecter4.setOnClickListener {
            setupPermissions()
            imageView4 = findViewById(R.id.imageView16)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            selected_img_order.add(imageView4)

        }
        val upload_data = findViewById<Button>(R.id.upload_all_data)
        upload_data.setOnClickListener {
            uploadData()
            val intent = Intent(this, Pashubazar::class.java)
            startActivity(intent)
        }

    }
    private fun uploadData(){
        // Create a storage reference from our app
        val storage = Firebase.storage
        var storageRef = storage.reference
        // Create a child reference
        for (elements in imagelist) {
            // Get the data from an ImageView as bytes
            var file = elements
            var imagesRef: StorageReference? = storageRef.child("animals/${file.lastPathSegment}")
            var uploadTask = imagesRef?.putFile(file)
            uploaded_images.add(imagesRef.toString())
            Log.d("iamgeRef", imagesRef.toString())
            // Register observers to listen for when the download is done or if it fails
            uploadTask?.addOnFailureListener {
                // Handle unsuccessful uploads
            }?.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
            }
        }

        price = findViewById<EditText>(R.id.price).text.toString()
        age = findViewById<EditText>(R.id.age).text.toString()
        val user = hashMapOf(
            "user_id" to user_uid,
            "animal" to animal,
            "breed" to breed,
            "age" to age,
            "byaat" to byaat,
            "milk_quantity" to milk_quantity,
            "milk_capacity" to milk_capacity,
            "price" to price,
            "img1" to uploaded_images[0],
            "img2" to uploaded_images[1],
            "img3" to uploaded_images[2],
            "img4" to uploaded_images[3]
        )
        db.collection("animal-details")
            //.document(user_uid)
            .add(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $user_uid") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }



        imagelist.clear()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imagelist.add(imageUri!!)
            if (!selected_img_order.isNullOrEmpty()) {
                Log.d("Elements", selected_img_order.last().toString())
                if (!imagelist.isNullOrEmpty()) {
                    selected_img_order.last().setImageURI(imagelist.last())
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permission Status", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        val RECORD_REQUEST_CODE = 101
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }
    companion object {
        private const val TAG = "FirebaseDatabase"
    }
}