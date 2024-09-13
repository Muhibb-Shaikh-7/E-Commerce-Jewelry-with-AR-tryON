package com.example.majorproject.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.adapters.ItemAdapter
import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.item
import com.example.majorproject.description.ProductDescription
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: MutableList<item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        Log.d("Check","Home Fragment Started ")

        // Initialize itemList as mutable
        itemList = mutableListOf()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycleview)
        Log.d("Check","RecyclerView Initialized ")
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = layoutManager

        // Initialize adapter with Glide support for loading images

        adapter = ItemAdapter(requireContext(), itemList) { clickedItem ->
            val intent = Intent(context, ProductDescription::class.java).apply {
            }
            startActivity(intent)
        }

        recyclerView?.adapter = adapter

        val collectionPath = "Products"

        // Add product data to Firestore (Optional, remove if already added)
        val product1 = createProduct1()
//        val product2 = createProduct2()

        Log.d("Check","product1 , product2 initialized ")

        addProductToFirestore( "Special-Occasion", product1)
//        addProductToFirestore( "Everyday-Fashion", product2)

        // Fetch product data from Firestore and add to RecyclerView
        fetchProductData(
            collectionPath = "Product",
            documentId = "Ring",
            collectionPath2 = "Women",
            documentId2 = "Special-Occasion",
            onSuccess = { product ->
                if (product != null) {
                    addDataInRecyclerView(product)
                } else {
                    Log.d("Check", "No product data found!")
                }
            },
            onFailure = { e ->
                Log.d("Check", "Error retrieving product: $e")
            }
        )

        return view
    }

    private fun addDataInRecyclerView(product: Product) {
        // Add product data to itemList
        product.images["0"]?.let {
            item(
               image= it, // Fetching the first image URL
                name = product.name,
                price = product.price
            )
        }?.let {
            itemList.add(
                it
            )
        }

        // Notify the adapter that data has been added
        adapter.notifyDataSetChanged()
    }

    private fun addProductToFirestore(
        documentId2: String, product: Product
    ) {

        Log.d("Check","add () called ")
        val firestore = FirebaseFirestore.getInstance()
        Log.d("Check","firestore initailized ")

        val productData = hashMapOf(
            "name" to product.name,
            "price" to product.price,
            "images" to product.images,
            "gross-weight" to product.grossWeight,
            "price-breaking" to product.priceBreaking,
            "product-specification" to product.productSpecification,
            "size" to product.size,
            "stock" to product.stock,
            "styling" to product.styling
        )
        Log.d("Check","Product data initialized")

        firestore.collection("Product")
            .document("Ring")
            .collection("Women")
            .document(documentId2)
            .set(productData)
            .addOnSuccessListener {
                Log.d("Check", "Product added successfully!")
            }
            .addOnFailureListener { e ->
                Log.d("Check", "Error adding product: $e")
            }
    }

    private fun fetchProductData(
        collectionPath: String,
        documentId: String,
        collectionPath2: String,
        documentId2: String,
        onSuccess: (Product?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        Log.d("Check","fetch () called")
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection(collectionPath)
            .document(documentId)
            .collection(collectionPath2)
            .document(documentId2)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.toObject(Product::class.java)
                    onSuccess(data)
                } else {
                    Log.d("Firestore", "No such document!")
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Example Product creation functions (Optional, remove if unnecessary)
    private fun createProduct1(): Product {
        return Product(
            name = "Avon Gleaming Diamond Ring", price = "₹74,624",
            images = mapOf(
               "0" to "gs://krishna-jewelry-app.appspot.com/product/ring/women/special occasion/2.4.jpeg",
              "1" to  "gs://krishna-jewelry-app.appspot.com/product/ring/women/special occasion/2.1.jpeg",
              " 2" to "gs://krishna-jewelry-app.appspot.com/product/ring/women/special occasion/2.2.jpeg",
                "3" to "gs:/krishna-jewelry-app.appspot.com/product/ring/women/special occasion/2.3.jpeg"
            ),
            grossWeight = mapOf("0" to "1.938",
                "1" to  "2.003",
                "2" to "1.864")
            , priceBreaking = mapOf(
                "Diamond" to "₹30,850",
                "Making Charges" to "₹2,713",
                "Metal" to "₹6,648",
                "Taxes" to "₹396",
                "Total" to "₹40,580"
            ),
            productSpecification = mapOf(
                "brand" to "Mahavir",
                "collection" to "Mahavir",
                "country-of-origin" to "India",
                "design-type" to "BASIC",
                "diamond-carat" to "0.20",
                "diamond-clarity" to "S12",
                "diamond-settings" to "Free",
                "diamond-weight" to "0.406",
                "gender" to "Women",
                "item-type" to "Finger Ring",
                "jewellery-type" to "Diamond Jewellery",
                "karatage" to "14",
                "material-color" to "Yellow Gold",
                "metal" to "Gold",
                "product-type" to "STUDDED"
            ),
            size = mapOf("0" to "16.40","1" to "17.40"," 2" to "1940"),
            stock = "5",
            styling = mapOf(
                "des" to "Set in 14 KT Yellow Gold with diamonds",
                "style" to "Wedding Party"
            )
        )
    }

//    private fun createProduct2(): Product {
//        return Product(
//            name = "Sparkling Diamond and Platinum Ring", price = "₹66,312",
//            images = arrayOf(
//                "gs://krishna-jewelry-app.appspot.com/product/ring/women/everyday fashion/3.3.jpeg",
//                "gs://krishna-jewelry-app.appspot.com/product/ring/women/everyday fashion/3.1.jpeg",
//                "gs://krishna-jewelry-app.appspot.com/product/ring/women/everyday fashion/3.2.jpeg",
//                "gs://krishna-jewelry-app.appspot.com/product/ring/women/everyday fashion/3.4.jpeg",
//                "gs://krishna-jewelry-app.appspot.com/product/ring/women/everyday fashion/3.5.jpeg"
//            ),
//            grossWeight = arrayOf("1.938", "2.003", "1.864"), priceBreaking = mapOf(
//                "Diamond" to "₹30,850",
//                "Making Charges" to "₹2,713",
//                "Metal" to "₹6,648",
//                "Taxes" to "₹396",
//                "Total" to "₹40,580"
//            ),
//            productSpecification = mapOf(
//                "brand" to "Mahavir",
//                "collection" to "Mahavir",
//                "country-of-origin" to "India",
//                "design-type" to "BASIC",
//                "diamond-carat" to "0.20",
//                "diamond-clarity" to "S12",
//                "diamond-settings" to "Free",
//                "diamond-weight" to "0.16",
//                "gender" to "Women",
//                "item-type" to "Finger Ring",
//                "jewellery-type" to "Diamond Jewellery",
//                "karatage" to "14",
//                "material-color" to "Silver",
//                "metal" to "Platinum",
//                "product-type" to "STUDDED"
//            ),
//            size = arrayOf("16.40", "17.40", "1940"), stock = "1",
//            styling = mapOf(
//                "des" to "KISNA 14k / 18k Real Gold & Diamond Ring",
//                "style" to "Special Occasion"
//            )
//        )
//    }
}