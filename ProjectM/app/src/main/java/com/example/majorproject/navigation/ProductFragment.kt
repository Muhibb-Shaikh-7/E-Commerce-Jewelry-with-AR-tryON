package com.example.majorproject.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.withContext

class ProductFragment : Fragment(),ItemAdapter.OnItemClickListener {

lateinit var recyclerView: RecyclerView
   private lateinit var itemList:MutableList<item>
lateinit var adapter:ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product, container, false)
     itemList= mutableListOf()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.most_gifted_recycler)




        recyclerView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

       adapter = ItemAdapter(requireContext(), itemList, this)
        recyclerView?.adapter = adapter


        fetchAllAuspiciousProducts()
      view.findViewById<ImageView>(R.id.banner1).setOnClickListener{
          navigateToCategoryActivity("necklace")
      }
      view.findViewById<ImageView>(R.id.banner2).setOnClickListener{
          navigateToCategoryActivity("ring")
      }
        view.findViewById<ImageView>(R.id.ring_explore).setOnClickListener{
            navigateToCategoryActivity("ring")
        }
        view.findViewById<ImageView>(R.id.necklace_explore).setOnClickListener{
            navigateToCategoryActivity("necklace")

        }
        view.findViewById<ImageView>(R.id.bracelet_explore).setOnClickListener{
            navigateToCategoryActivity("bracelet")
        }

        return view
    }
    private fun fetchAllAuspiciousProducts() {
        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Convert itemType2 and selectedGender to lowercase and fetch from the specified path
                val productTypeRef = db.collection("Product")
                    .document("Rings")
                    .collection("women")
                    .document("Auspicious")
                    .collection("Items")

                // Fetch all items within the specified "Items" collection
                val itemsSnapshot = productTypeRef.get().await()
                if (itemsSnapshot.isEmpty) {
                    Log.e(
                        "fetchAllProducts",
                        "No items found in specified path: "
                    )
                    return@launch
                }

                for (itemDoc in itemsSnapshot.documents) {
                    if (itemDoc.exists()) {
                        val grossWeightMap = itemDoc.get("grossWeight") as? Map<*, *>
                        val imagesMap = itemDoc.get("images") as? Map<*, *>
                        val priceBreakingMap = itemDoc.get("priceBreaking") as? Map<*, *>
                        val specificationMap = itemDoc.get("productSpecification") as? Map<*, *>
                        val stylingMap = itemDoc.get("styling") as? Map<*, *>

                        // Construct the product object
                        val product = Product(
                            name = itemDoc.getString("productName") ?: "",
                            price = itemDoc.getString("productPrice") ?: "",
                            images = mapOf(
                                "0" to (imagesMap?.get("0").toString() ?: ""),
                                "1" to (imagesMap?.get("1").toString() ?: ""),
                                "2" to (imagesMap?.get("2").toString() ?: ""),
                                "3" to (imagesMap?.get("3").toString() ?: "")
                            ),
                            grossWeight = mapOf(
                                "diamond-weight" to (grossWeightMap?.get("diamond-weight") ?: "")
                            ),
                            priceBreaking = mapOf(
                                "Diamond" to (priceBreakingMap?.get("Diamond").toString() ?: ""),
                                "Making Charges" to (priceBreakingMap?.get("Making Charges")
                                    .toString() ?: ""),
                                "Metal" to (priceBreakingMap?.get("Metal").toString() ?: ""),
                                "Taxes" to (priceBreakingMap?.get("Taxes").toString() ?: ""),
                                "Total" to (itemDoc.getString("price") ?: "")
                            ),
                            productSpecification = mapOf(
                                "Brand" to "Mahavir",
                                "collection" to (priceBreakingMap?.get("collection").toString()
                                    ?: ""),
                                "design-type" to (priceBreakingMap?.get("design-type").toString()
                                    ?: ""),
                                "diamond-clarity" to "22k",
                                "diamond-settings" to "121k",
                                "country-of-origin" to "india",
                                "diamond-carat" to (specificationMap?.get("diamond-carat")
                                    .toString() ?: ""),
                                "diamond-weight" to (specificationMap?.get("diamond-weight")
                                    .toString() ?: ""),
                                "gender" to (specificationMap?.get("gender").toString() ?: ""),
                                "item-type" to (specificationMap?.get("item-type").toString()
                                    ?: ""),
                                "jewellery-type" to (specificationMap?.get("jewellery-type")
                                    .toString() ?: ""),
                                "karatage" to (specificationMap?.get("karatage").toString() ?: ""),
                                "material-color" to (specificationMap?.get("material-color")
                                    .toString() ?: ""),
                                "metal" to (specificationMap?.get("metal").toString() ?: "")
                            ),
                            size = mapOf("size" to "5mm"),
                            stock = itemDoc.getString("stockQuantity") ?: "",
                            styling = mapOf(
                                "style" to (stylingMap?.get("style").toString() ?: "")
                            )
                        )

                        // Create an item for the adapter
                        val item1 = product.images["0"]?.let {
                            Log.d("fetchAllProducts", "Product images: ${product.images}")
                            item(
                                image = it,
                                name = product.name,
                                price = product.price,
                                style = product.styling["style"] ?: "",
                                product = product
                            )
                        }

                        // Add the item to the list and notify the adapter
                        if (item1 != null) {
                            itemList.add(item1)
                        }

                        withContext(Dispatchers.Main) {
                            adapter.notifyDataSetChanged()
                        }
                        Log.d("fetchAllProducts", "Fetched item: ${product.name}")
                    } else {
                        Log.w("fetchAllProducts", "Document does not exist: ${itemDoc.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e("fetchAllProducts", "Error fetching products", e)
            }
        }
    }

    override fun onItemClick(clickedItem: item) {
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
    private fun navigateToCategoryActivity(category: String) {
        val intent = Intent(context, Categories::class.java)
        intent.putExtra("selected_category", category)
        startActivity(intent)
    }

}

