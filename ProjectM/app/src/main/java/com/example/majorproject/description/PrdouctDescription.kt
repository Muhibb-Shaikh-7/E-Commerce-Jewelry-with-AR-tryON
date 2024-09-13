package com.example.majorproject.description

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majorproject.R
import com.example.majorproject.RingSizeCalculator
import com.example.majorproject.adapters.ProductSpecificationAdapter
import com.example.majorproject.adapters.SizeItemAdapter
import com.example.majorproject.adapters.WeightItemAdapter
import com.example.majorproject.dataClass.ProductSpecification
import com.example.majorproject.databinding.ActivityProductDescriptionBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductDescription : AppCompatActivity() {

    private lateinit var binding: ActivityProductDescriptionBinding
    private val firestore = FirebaseFirestore.getInstance()
    var calculatedRingSize: String? = null
    private val RING_SIZE_CALCULATOR_REQUEST_CODE = 1
    private var isPriceBreakExpanded = false
    private var isProductDescExpanded = false
    private var isSizeWeightExpanded = false
    private var isStylingExpanded = false
    private lateinit var adapter: ProductSpecificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("Check", "Activity initialized")


        fetchProductData()
        fetchStockFromFirebase()

        setupOnClickListeners()

            setProductSpecificationRecyclerView()
        }

        private fun setProductSpecificationRecyclerView() {
            val specList = listOf(
                ProductSpecification("Brand", "Mahavir"),
                ProductSpecification("Collection", "Mahavir"),
                ProductSpecification("Country of Origin", "India"),
                ProductSpecification("Design Type", "BASIC"),
                ProductSpecification("Diamond Carat", "0.20"),
                ProductSpecification("Diamond Clarity", "S12"),
                ProductSpecification("Diamond Settings", "Free"),
                ProductSpecification("Diamond Weight", "0.22"),
                ProductSpecification("Gender", "Women"),
                ProductSpecification("Item Type", "Finger Ring"),
                ProductSpecification("Jewellery Type", "Diamond Jewellery"),
                ProductSpecification("Karatage", "14"),
                ProductSpecification("Material Color", "Yellow Gold"),
                ProductSpecification("Metal", "Gold"),
                ProductSpecification("Product Type", "STUDDED")
            )

            adapter = ProductSpecificationAdapter(specList)
            binding.productSpecificationRV.layoutManager = LinearLayoutManager(this)
            binding.productSpecificationRV.adapter = adapter
        }



    private fun setupOnClickListeners() {

        binding.stylingLayout.setOnClickListener {
            toggleExpandableLayout(
                isStylingExpanded,
                binding.stylingExtandableLayout,
                binding.downArrowStyling,
                binding.stylingView
            )
            isStylingExpanded = !isStylingExpanded
        }

        binding.productDesLayout.setOnClickListener {
            toggleExpandableLayout(
                isProductDescExpanded,
                binding.productDesExtandableLayout,
                binding.downArrowProductDes,
                binding.productDesSubtitle
            )
            isProductDescExpanded = !isProductDescExpanded
        }

        binding.priceBreakLayout.setOnClickListener {
            toggleExpandableLayout(
                isPriceBreakExpanded,
                binding.priceBreakExtandableLayout,
                binding.downArrowPriceBreak,
                binding.priceBreakSubtitle
            )
            isPriceBreakExpanded = !isPriceBreakExpanded
        }

        binding.sizeAndWeightLayout.setOnClickListener {
            toggleExpandableLayout(
                isSizeWeightExpanded,
                binding.sizeAndWeightExtandableLayout,
                binding.downArrowSizeWeight,
                binding.sizeInMmAndGrOssWeightInGms
            )
            isSizeWeightExpanded = !isSizeWeightExpanded
        }

        binding.storeLocationButton.setOnClickListener {
            try {
                // Check if storeLocationLayout and outerScrollView are properly initialized
                if (binding.storeLocationLayout != null && binding.outerScrollView != null) {
                    // Get the original background color from colors.xml
                    val originalColor = ContextCompat.getColor(this, R.color.white)

                    // Set the background color to transparent from colors.xml
                    binding.storeLocationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

                    // Scroll the outerScrollView to the position of storeLocationLayout
                    binding.outerScrollView.post {
                        val targetPosition = binding.storeLocationLayout.top
                        Log.d("Check", "Scrolling to position: $targetPosition")

                        // Ensure the position is not negative or too large
                        if (targetPosition >= 0 && targetPosition <= binding.outerScrollView.height) {
                            binding.outerScrollView.smoothScrollTo(0, targetPosition)
                        } else {
                            Log.e("Check", "Invalid scroll position: $targetPosition")
                        }
                    }

                    // Delay for a few seconds, then change the background color back to the original
                    lifecycleScope.launch {
                        delay(2000) // Delay for 2 seconds (2000 milliseconds)
                        val animator = ObjectAnimator.ofArgb(
                            binding.storeLocationLayout,
                            "backgroundColor",
                            ContextCompat.getColor(this@ProductDescription, R.color.transparent),
                            originalColor
                        )
                        animator.duration = 1000 // Duration of the fade-in effect in milliseconds
                        animator.start()
                    }
                } else {
                    Log.e("Check", "Null reference: storeLocationLayout or outerScrollView is null")
                }
            } catch (e: NullPointerException) {
                // Log the null pointer exception with a message
                Log.e("Check", "NullPointerException in setStoreLocationButton: ${e.message}")
            } catch (e: Exception) {
                // Catch any other potential exceptions
                Log.e("Check", "Exception in setStoreLocationButton: ${e.message}")
            }
        }

        binding.sizeCalculatorBtn.setOnClickListener {
            val intent = Intent(this, RingSizeCalculator::class.java)
            startActivityForResult(intent, RING_SIZE_CALCULATOR_REQUEST_CODE)

        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RING_SIZE_CALCULATOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            calculatedRingSize=null
            calculatedRingSize = data?.getStringExtra("ringSize")
            displayRingSize(calculatedRingSize)

            Log.d("ringSize","Ring:${calculatedRingSize}")
        }
    }

    private fun fetchProductData() {
        Log.d("Check", "Fetching product data")
        firestore.collection("Products")
            .document("Rings")
            .collection("Women")
            .document("Daily-Wear")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("Check", "Document data: ${document.data}")
                    processDocumentData(document)
                } else {
                    Log.d("Check", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Check", "Error fetching document: ${e.message}")
            }
    }

    private fun processDocumentData(document: DocumentSnapshot) {
        try {
            // Size List
            val sizeList = document.get("size") as? List<*>
            if (sizeList != null && sizeList.isNotEmpty()) {
                val sizeAdapter = SizeItemAdapter(sizeList)
                binding.sizeRecycleView.adapter = sizeAdapter
                binding.sizeRecycleView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                Log.d("Firestore", "Size list populated")
            } else {
                Log.d("Firestore", "Size list is null or empty")
            }

            // Weight List
            val weightList = document.get("gross-weight") as? List<*>
            if (weightList != null && weightList.isNotEmpty()) {
                val weightAdapter = WeightItemAdapter(weightList)
                binding.weightRecycleView.adapter = weightAdapter
                binding.weightRecycleView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                Log.d("Firestore", "Weight list populated")
            } else {
                Log.d("Firestore", "Weight list is null or empty")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error processing document data: ${e.message}")
        }
    }

    private fun displayRingSize(calculatedRingSize: String?) {
        if (calculatedRingSize != null) {
            Log.d("ringSize", "Size: $calculatedRingSize")

            // Make sure the layout is visible
            binding.calculatedSizeLayout.visibility = View.VISIBLE

            // Fetch the product data asynchronously
            firestore.collection("Products")
                .document("Rings")
                .collection("Women")
                .document("Daily-Wear")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve the size list from the document
                        val sizeList = document.get("size") as? List<*>
                        if (sizeList != null && sizeList.isNotEmpty()) {
                            if (sizeList.contains(calculatedRingSize)) {
                                // If a match is found, display the calculated ring size
                                binding.calculatedSizeDes.text = "Available Ring Size: $calculatedRingSize"
                            } else {
                                // If no match is found, display a not available message
                                binding.calculatedSizeDes.text =
                                    "Oops! Your ring size $calculatedRingSize is not available"
                            }
                        } else {
                            // Handle the case where the size list is null or empty
                            binding.calculatedSizeDes.text =
                                "Size list is not available or empty"
                        }
                    } else {
                        // Handle the case where the document is not found
                        binding.calculatedSizeDes.text = "Document not found"
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors during the Firestore operation
                    Log.e("Firestore", "Error fetching document: ${e.message}")
                    binding.calculatedSizeDes.text = "Error fetching size data"
                }
        } else {
            // Hide the calculated size layout if no size is selected
            binding.calculatedSizeLayout.visibility = View.GONE
        }
    }


    private fun fetchStockFromFirebase() {
        Log.d("Check", "Fetching stock data")
        firestore.collection("Products")
            .document("Rings")
            .collection("Women")
            .document("Daily-Wear")
            .get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null && document.exists()) {
                        val stockString = document.getString("stock") ?: "0"
                        try {
                            val stock = stockString.toInt()
                            if (stock <= 10) {
                                binding.stockAvailableLayout.visibility = View.VISIBLE
                                binding.stockAvailableTxt.text = "Only $stock left in stock"
                                Log.d("Stock", "Stock available: $stock")
                            } else {
                                binding.stockAvailableLayout.visibility = View.GONE
                            }
                        }catch (e: NumberFormatException) {
                            Log.e("Check", "Error converting stock value to number: ${e.message}")
                        }
                    } else {
                        Log.d("Stock", "No such document or stock is null")
                    }
                } catch (e: Exception) {
                    Log.e("Stock", "Error processing stock data: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Stock", "Error fetching stock data: ${e.message}")
            }
    }

    private fun toggleExpandableLayout(
        isExpanded: Boolean,
        expandableLayout: View,
        arrowView: View,
        desView: View
    ) {
        if (isExpanded) {
            expandableLayout.visibility = View.GONE
            desView.visibility = View.VISIBLE
            arrowView.animate().rotation(0f).start()
        } else {
            expandableLayout.visibility = View.VISIBLE
            desView.visibility = View.GONE
            arrowView.animate().rotation(180f).start()
        }
    }
}
