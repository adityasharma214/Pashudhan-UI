package com.example.pashu_dhan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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


class Pashusell : AppCompatActivity() {
    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var imageView3: ImageView
    lateinit var imageView4: ImageView
    private var image2uri: Uri? = null
    private var image3uri: Uri? = null
    private var image4uri: Uri? = null
    private val pickImage = 100
    private var imageUri: Uri? = null
    var imagelist = ArrayList<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pashusell)
//        animal list spinner adapter
        val animal_list = resources.getStringArray(R.array.Animals)
        val which_animal_spinner = findViewById<Spinner>(R.id.spinner_which_animal)
        if (which_animal_spinner != null){
            val adapter = ArrayAdapter(this, R.layout.spinner_item, animal_list)
            which_animal_spinner.adapter = adapter
            which_animal_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + animal_list[position], Toast.LENGTH_SHORT).show()
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
        if (breed_spinner != null){
            val breed_adapter = ArrayAdapter(this, R.layout.spinner_item, breed_list)
            breed_spinner.adapter = breed_adapter
            breed_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + breed_list[position], Toast.LENGTH_SHORT).show()
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
        if (byaat_spinner != null){
            val byaat_adapter = ArrayAdapter(this, R.layout.spinner_item, byaat_list)
            byaat_spinner.adapter = byaat_adapter
            byaat_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + byaat_list[position], Toast.LENGTH_SHORT).show()
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
        if (milk_quantity_spinner != null){
            val milk_quantity_adapter = ArrayAdapter(this, R.layout.spinner_item, milk_quantity_list)
            milk_quantity_spinner.adapter = milk_quantity_adapter
            milk_quantity_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + milk_quantity_list[position], Toast.LENGTH_SHORT).show()
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
        if (milk_capacity_spinner != null){
            val milk_capacity_adapter = ArrayAdapter(this, R.layout.spinner_item, milk_capacity_list)
            milk_capacity_spinner.adapter = milk_capacity_adapter
            milk_capacity_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    Toast.makeText(this@Pashusell,
                        getString(R.string.selected_item) + " " +
                                "" + milk_capacity_list[position], Toast.LENGTH_SHORT).show()
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
            if (!imagelist.isNullOrEmpty()) {
                imageView1.setImageURI(imagelist.last())
            }
        }
        open_image_delecter2.setOnClickListener {
            setupPermissions()
            imageView2 = findViewById(R.id.imageView2)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            if (!imagelist.isNullOrEmpty()) {
                imageView2.setImageURI(imagelist.last())
            }
        }
        open_image_delecter3.setOnClickListener {
            setupPermissions()
            imageView3 = findViewById(R.id.imageView15)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            if (!imagelist.isNullOrEmpty()) {
                imageView3.setImageURI(imagelist.last())
            }
        }
        open_image_delecter4.setOnClickListener {
            setupPermissions()
            imageView4 = findViewById(R.id.imageView16)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
            if (!imagelist.isNullOrEmpty()) {
                imageView4.setImageURI(imagelist.last())
            }
        }
        val upload_data = findViewById<Button>(R.id.upload_all_data)
        upload_data.setOnClickListener{
            imagelist.clear()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imagelist.add(imageUri!!)
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
}