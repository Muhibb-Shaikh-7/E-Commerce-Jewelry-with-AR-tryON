package com.example.majorproject.navigation

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
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: MutableList<item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize itemList and adapter
        itemList = mutableListOf()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleview)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = ItemAdapter(requireContext(), itemList)
        recyclerView.adapter = adapter

        // Fetch product data from Firestore for Women categories
        fetchWomenCategoriesData(
            collectionPath = "Products",
            documentId = "Ring",
            collectionPath2 = "Women",
            onSuccess = { products ->
                for (product in products) {
                    addDataInRecyclerView(product)
                }
            },
            onFailure = { e ->
                Log.e("Firestore", "Error fetching Women categories data: $e")
            }
        )

        return view
    }

    private fun addDataInRecyclerView(product: Product) {
        // Add product data to itemList
        product.images["0"]?.let {
            item(
                image = it, // Fetching the first image URL
                name = product.name,
                price = product.price
            )
        }?.let {
            itemList.add(it)
        }

        // Notify the adapter that data has been added
        adapter.notifyDataSetChanged()
    }

    private fun fetchWomenCategoriesData(
        collectionPath: String,
        documentId: String,
        collectionPath2: String,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val womenCollectionRef = db.collection(collectionPath)
            .document(documentId)
            .collection(collectionPath2)

        womenCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                val productList = mutableListOf<Product>()
                for (document in querySnapshot) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                onSuccess(productList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
