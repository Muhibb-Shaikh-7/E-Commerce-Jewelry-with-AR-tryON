package com.example.majorproject

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TrackOrder : AppCompatActivity() {

    private lateinit var orderNumberTextView: TextView
    private lateinit var orderDateTextView: TextView
    private lateinit var shippingAddressTextView: TextView
    private lateinit var carrierInfoTextView: TextView

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        // Initialize Views
        orderNumberTextView = findViewById(R.id.orderNumber)
        orderDateTextView = findViewById(R.id.orderDate)
        shippingAddressTextView = findViewById(R.id.shippingAddress)
        carrierInfoTextView = findViewById(R.id.carrierInfo)

        // Fetch order details
        fetchOrderDetails("order_id_here")
    }

    private fun fetchOrderDetails(orderId: String) {
        db.collection("orders").document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val orderNumber = document.getString("orderNumber") ?: "N/A"
                    val orderDate = document.getString("orderDate") ?: "N/A"
                    val shippingAddress = document.getString("shippingAddress") ?: "N/A"
                    val carrierInfo = document.getString("carrierInfo") ?: "N/A"

                    // Update UI with fetched data
                    orderNumberTextView.text = orderNumber
                    orderDateTextView.text = "Placed on: $orderDate"
                    shippingAddressTextView.text = shippingAddress
                    carrierInfoTextView.text = carrierInfo
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch order details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
