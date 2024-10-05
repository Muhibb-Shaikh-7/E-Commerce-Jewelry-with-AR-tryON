
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
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await

    class HomeFragment : Fragment() {

        private lateinit var adapter: ItemAdapter
        private lateinit var itemList: MutableList<item>

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)

            // Initialize itemList as mutable
            itemList = mutableListOf()

            // Initialize RecyclerView
            val recyclerView = view?.findViewById<RecyclerView>(R.id.recycleview)
            recyclerView?.layoutManager = GridLayoutManager(context, 2) // Grid layout with 2 columns

            // Initialize adapter with the itemList
            adapter = ItemAdapter(requireContext(), itemList)
            recyclerView?.adapter = adapter

            // Fetch data from Firestore categories
            fetchAllAuspiciousProducts()

            return view
        }

        // Function to fetch and combine data from all categories
        private fun fetchAllAuspiciousProducts() {
            val db = FirebaseFirestore.getInstance()

            // Coroutine to handle asynchronous Firestore operations
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Fetch the main "Auspicious" collection
                    val auspiciousDocRef = db.collection("Products")
                        .document("Rings")
                        .collection("Women")
                        .document("Auspicious")

                    // Loop through item1 to item10 sub-collections
                    for (i in 1..10) {
                        val subCollectionRef = auspiciousDocRef.collection("item$i")

                        // Fetch all documents from the current sub-collection
                        val querySnapshot = subCollectionRef.get().await()

                        // Iterate over each document in the sub-collection
                        for (document in querySnapshot.documents) {
                            if (document.exists()) {
                                // Parse the document to a Product object
                                val product = document.toObject(Product::class.java)
                                if (product != null) {

                                    val item1 = item(
                                        image = product.images["0"]
                                            ?: "", // Get the first image
                                        name = product.name,
                                        price = product.price,
                                        style = product.styling["style"] ?: ""
                                    )

                                    // Add the item to the list
                                    itemList.add(item1)

                                }
                            }
                        }
                    }

                    // Update RecyclerView on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    Log.e("check", "Error fetching products: $e")
                }
            }
        }


    }