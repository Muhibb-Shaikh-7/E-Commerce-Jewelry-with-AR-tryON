package com.example.majorproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class OrderConfirmationActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var orderIdTextView: TextView
    private lateinit var trackIdTextView: TextView
    private lateinit var orderDetailsTextView: TextView
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)
        firestore = FirebaseFirestore.getInstance()

        orderIdTextView = findViewById(R.id.orderIdTextView)
        trackIdTextView = findViewById(R.id.trackIdTextView)
        orderDetailsTextView = findViewById(R.id.orderDetailsTextView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)

        processOrder()
    }

    private fun processOrder() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail == null) {
            orderDetailsTextView.text = "User not authenticated"
            return
        }

        firestore.collection("users").document(currentUserEmail).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val orderId = UUID.randomUUID().toString()
                val trackId = UUID.randomUUID().toString()
                var total = 0.0
                val orderDetails = StringBuilder("Order Details:\n\n")

                val orderItems = mutableListOf<Map<String, Any>>()
                for (document in result) {
                    val itemData = document.data

                    val name = itemData["productName"] as? String ?: "Unknown Product"
                    val price = (itemData["price"] as? Double) ?: 0.0
                    val quantity = (itemData["quantity"] as? Long)?.toInt() ?: 1
                    val subTotal = price * quantity

                    orderDetails.append("• $name (x$quantity): RS. ${"%.2f".format(subTotal)}\n")
                    orderItems.add(mapOf("productName" to name, "price" to price, "quantity" to quantity))
                    total += subTotal
                }

                val tax = (total * 18) / 100 // Assuming 18% tax
                val deliveryFee = 500.0 // Flat delivery fee
                val totalAmount = (total + tax + deliveryFee).toLong()

                val orderData = mapOf(
                    "orderId" to orderId,
                    "trackId" to trackId,
                    "userEmail" to currentUserEmail,
                    "items" to orderItems,
                    "subTotal" to total,
                    "tax" to tax,
                    "deliveryFee" to deliveryFee,
                    "totalAmount" to totalAmount
                )

                firestore.collection("orders").document(orderId)
                    .set(orderData)
                    .addOnSuccessListener {
                        orderIdTextView.text = "Order ID: $orderId"
                        trackIdTextView.text = "Track ID: $trackId"
                        orderDetailsTextView.text = orderDetails.toString()
                        totalAmountTextView.text = "Total Amount: RS. ${"%.2f".format(totalAmount.toDouble())}"
                    }
                    .addOnFailureListener { e ->
                        orderDetailsTextView.text = "Failed to save order: ${e.message}"
                    }
            }
            .addOnFailureListener { e ->
                orderDetailsTextView.text = "Failed to fetch cart items: ${e.message}"
            }
    }

}
