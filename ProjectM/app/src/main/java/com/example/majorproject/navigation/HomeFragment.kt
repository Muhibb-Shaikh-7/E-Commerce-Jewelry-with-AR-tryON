package com.example.majorproject.navigation

import android.os.Bundle
import android.util.Log
import android.content.Intent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.Serializable

class HomeFragment : Fragment(), ItemAdapter.OnItemClickListener {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: MutableList<item>
    private var product: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        itemList = mutableListOf()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycleview)
        recyclerView?.layoutManager = GridLayoutManager(context, 2)

        adapter = ItemAdapter(requireContext(), itemList, this)
        recyclerView?.adapter = adapter

        fetchAllAuspiciousProducts()

        return view
    }

    private fun fetchAllAuspiciousProducts() {
        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val auspiciousDocRef = db.collection("Products")
                    .document("Rings")
                    .collection("Women")
                    .document("Auspicious")

                for (i in 1..10) {
                    val subCollectionRef = auspiciousDocRef.collection("item$i")
                    val querySnapshot = subCollectionRef.get().await()

                    for (document in querySnapshot.documents) {
                        if (document.exists()) {
                            val fetchedProduct = document.toObject(Product::class.java)

                            Log.d("check", "Fetched document: ${document.data}")
                            if (fetchedProduct != null) {
                                val product = Product(
                                    name = document.getString("name") ?: "",
                                    price = document.getString("price") ?: "",
                                    images = document.get("images") as? Map<String, String> ?: emptyMap(),
                                    grossWeight = document.get("gross-weight") as? Map<String, String> ?: emptyMap(),
                                    priceBreaking = document.get("price-breaking") as? Map<String, String> ?: emptyMap(),
                                    productSpecification = document.get("product-specification") as? Map<String, String> ?: emptyMap(),
                                    size = document.get("size") as? Map<String, String> ?: emptyMap(),
                                    stock = document.getString("stock") ?: "",
                                    styling = document.get("styling") as? Map<String, String> ?: emptyMap()
                                )

                                val item1 = item(
                                    image = fetchedProduct.images["0"] ?: "",
                                    name = fetchedProduct.name,
                                    price = fetchedProduct.price,
                                    style = fetchedProduct.styling["style"] ?: "",
                                    product = product // Pass the Product object here
                                )
                                itemList.add(item1)
                            }

                        }
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Log.e("check", "Error fetching products: $e")
            }
        }
    }

    override fun onItemClick(clickedItem: item) {
        // Access the Product directly from clickedItem
        val selectedProduct = clickedItem.product

        if (selectedProduct != null) {
            // Create the intent for ProductDescription activity
            val intent = Intent(requireContext(), ProductDescription::class.java)
            intent.putExtra("product", selectedProduct) // Pass the Product object
            Log.d("ProductDescription", "Product passed: ${selectedProduct.name}")
            startActivity(intent)
        } else {
            Log.d("ProductDescription", "Selected product not found")
        }
    }



}
