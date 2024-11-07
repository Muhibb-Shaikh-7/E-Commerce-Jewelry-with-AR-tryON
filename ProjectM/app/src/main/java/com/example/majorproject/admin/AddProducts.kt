package com.example.majorproject.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.majorproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddProducts : AppCompatActivity() {

    private lateinit var productImageView1: ImageView
    private lateinit var productImageView2: ImageView
    private lateinit var productImageView3: ImageView
    private lateinit var productImageView4: ImageView
    private lateinit var btnSaveProduct: Button
    private lateinit var spinnerProductCategoryGen: Spinner
    private lateinit var spinnerProductCategory: Spinner

    private var selectedGender: String = "Women"
    private var selectedCategory: String = "Auspicious"
    private lateinit var storageReference: StorageReference

    // Store each selected image URI
    private val imageUris = arrayOfNulls<Uri>(4)
    private val imageUrls = arrayOfNulls<String>(4) // Store URLs to save in Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_products)

        spinnerProductCategory = findViewById(R.id.spinnerProductCategory)
        spinnerProductCategoryGen = findViewById(R.id.spinnerProductCategoryGen)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)

        productImageView1 = findViewById(R.id.img1)
        productImageView2 = findViewById(R.id.img2)
        productImageView3 = findViewById(R.id.img3)
        productImageView4 = findViewById(R.id.img4)
        setupPermissionAndListeners()

        btnSaveProduct.setOnClickListener {
            uploadAllImagesAndSaveDetails()
        }
        val categories = listOf("Auspicious", "Special-Occasion", "Daily-Wear", "Everyday-Fashion") // Your category options
        val genders = listOf("Men", "Women", "Kid") // Your gender options

        // Initialize category spinner
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCategory.adapter = categoryAdapter

        // Initialize gender spinner
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCategoryGen.adapter = genderAdapter

        spinnerProductCategoryGen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGender = genders[position] // Update selected gender based on user selection
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Keep default or handle no selection if needed
            }
        }

        spinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position] // Update selected category based on user selection
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Keep default or handle no selection if needed
            }
        }
    }

    private fun setupPermissionAndListeners() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }

        productImageView1.setOnClickListener { openGallery(0) }
        productImageView2.setOnClickListener { openGallery(1) }
        productImageView3.setOnClickListener { openGallery(2) }
        productImageView4.setOnClickListener { openGallery(3) }
    }

    private fun openGallery(imageIndex: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000 + imageIndex) // requestCode differentiates images
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val index = requestCode - 1000
            val uri = data.data
            imageUris[index] = uri

            // Set the image preview
            when (index) {
                0 -> productImageView1.setImageURI(uri)
                1 -> productImageView2.setImageURI(uri)
                2 -> productImageView3.setImageURI(uri)
                3 -> productImageView4.setImageURI(uri)
            }
        }
    }

    private fun uploadAllImagesAndSaveDetails() {
        updateStorageReference()

        // Upload each image one by one
        imageUris.forEachIndexed { index, uri ->
            uri?.let {
                uploadImage(uri, index) { imageUrl ->
                    imageUrls[index] = imageUrl

                    // When all URLs are uploaded, save product details
                    if (imageUrls.all { it != null }) {
                        saveProductDetails()
                    }
                }
            }
        }
    }

    private fun uploadImage(uri: Uri, index: Int, onSuccess: (String) -> Unit) {
        val fileName = "product_image_${System.currentTimeMillis()}_$index"
        val imageRef = storageReference.child(fileName)

        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStorageReference() {
        storageReference = FirebaseStorage.getInstance().reference
            .child("product/ring/$selectedGender/$selectedCategory")
    }

    private fun saveProductDetails() {
        // Get product details from EditTexts and other views
        val productName = findViewById<EditText>(R.id.etProductName).text.toString()?:"0"
        val productPrice = findViewById<EditText>(R.id.etProductPrice).text.toString()?: "0"
        val stockQuantity = findViewById<EditText>(R.id.etProductStock).text.toString() ?: "0"

        // Assuming the other details are extracted similarly:
        val availableSizes = "5.00mm" // You can retrieve this from a corresponding EditText if necessary
        val diamondWeight = findViewById<EditText>(R.id.etDiamondWeight).text.toString()?: "0.0"
        val diamondCarat = findViewById<EditText>(R.id.etDiamondCarat).text.toString()?: "0.0"
        val gender = findViewById<EditText>(R.id.etGender).text.toString()
        val itemType = findViewById<EditText>(R.id.etItemType).text.toString()
        val jewelleryType = findViewById<EditText>(R.id.etJewelleryType).text.toString()
        val karatage = findViewById<EditText>(R.id.etKaratage).text.toString()?: "0"
        val materialColor = findViewById<EditText>(R.id.etMaterialColor).text.toString()
        val metal = findViewById<EditText>(R.id.etMetal).text.toString()

        // Image URLs (assuming they are uploaded correctly before saving)


        // Define the product map structure
        val product = hashMapOf(
            "productName" to productName,
            "productPrice" to productPrice, // Stored as String
            "stockQuantity" to stockQuantity, // Stored as String
            "availableSizes" to availableSizes, // Stored as String

            "images" to mapOf("0" to imageUrls.first(),"1" to imageUrls[1],"2" to imageUrls[2],"3" to imageUrls[3] ),
            "grossWeight" to mapOf(
                "diamond-weight" to diamondWeight, // Stored as String
             // Stored as String
            ),
            "priceBreaking" to mapOf(
                "Diamond" to productPrice, // Stored as String
                "collection" to findViewById<EditText>(R.id.etCollection).text.toString(),
                "Making Charges" to findViewById<EditText>(R.id.etMakingCharges).text.toString(),
                "Metal" to findViewById<EditText>(R.id.etMetalPrice).text.toString(),
                "Taxes" to findViewById<EditText>(R.id.etTaxes).text.toString()
            ),
            "productSpecification" to mapOf(
                "diamond-carat" to diamondCarat,
                "diamond-weight" to diamondWeight,
                "gender" to gender,
                "item-type" to itemType,
                "jewellery-type" to jewelleryType,
                "karatage" to karatage, // Stored as String
                "material-color" to materialColor,
                "metal" to metal
            ),
            "size" to mapOf("size" to availableSizes),
            "styling" to mapOf("design-type" to findViewById<EditText>(R.id.etDesignType).text.toString())
        )
        val itemType1= product["productSpecification"] as Map<*,*>
        val itemType2= itemType1["item-type"]

        // Define the Firestore reference and save the product
        val productReference = FirebaseFirestore.getInstance()
            .collection("Products")
            .document(itemType2.toString().toLowerCase())
            .collection(selectedGender)
            .document(selectedCategory)
            .collection("Items")
            .document()

        productReference.set(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                clearFields() // Reset fields after saving
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.etProductName).text.clear()
        findViewById<ImageView>(R.id.img1).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img2).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img3).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img4).setImageResource(R.drawable.ic_placeholder_image)
        imageUris.fill(null)
        imageUrls.fill(null)
    }

}