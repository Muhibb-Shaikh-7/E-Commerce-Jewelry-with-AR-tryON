package com.example.majorproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.dataClass.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {

    companion object {
        const val UPI_PAYMENT_REQUEST_CODE = 123
        const val UPI_ID = "mohdirfanulhaque23106@okicici"
        const val MERCHANT_NAME = "Mahvir Gems"
        const val TRANSACTION_NOTE = "Transaction for Order"
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve passed data
        val totalAmount: Double = intent.getDoubleExtra("TOTAL", 1.00)
        val cartItems: ArrayList<CartItem>? = intent.getParcelableArrayListExtra("CART_ITEMS")
        val subTotal: Double = intent.getDoubleExtra("SUB_TOTAL", 0.00)
        val tax: Double = intent.getDoubleExtra("TAX", 0.00)
        val delivery: Double = intent.getDoubleExtra("DELIVERY", 0.00)

        // Initialize buttons
        val payButton: Button = findViewById(R.id.pay_button)
        val testButton: Button = findViewById(R.id.test_button)

        payButton.setOnClickListener {
            initiateUpiPayment(totalAmount)
        }

        testButton.setOnClickListener {
            // Directly save test payment to Firestore
            onPaymentSuccess(
                cartItems, subTotal, tax, delivery, totalAmount,
                "Test Order", System.currentTimeMillis()
            )
        }
    }

    private fun initiateUpiPayment(amount: Double) {
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", UPI_ID)
            .appendQueryParameter("pn", MERCHANT_NAME)
            .appendQueryParameter("tn", TRANSACTION_NOTE)
            .appendQueryParameter("am", amount.toString())
            .appendQueryParameter("cu", "INR")
            .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri

        try {
            startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "No UPI app found on your device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
                val response = data?.getStringExtra("response")
                if (response != null && response.contains("SUCCESS", true)) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                    val cartItems: ArrayList<CartItem>? = intent.getParcelableArrayListExtra("CART_ITEMS")
                    val subTotal: Double = intent.getDoubleExtra("SUB_TOTAL", 0.00)
                    val tax: Double = intent.getDoubleExtra("TAX", 0.00)
                    val delivery: Double = intent.getDoubleExtra("DELIVERY", 0.00)
                    val totalAmount: Double = intent.getDoubleExtra("TOTAL", 1.00)

                    onPaymentSuccess(cartItems, subTotal, tax, delivery, totalAmount, "Confirmed", System.currentTimeMillis())
                } else {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPaymentSuccess(
        cartItems: ArrayList<CartItem>?,
        subTotal: Double,
        tax: Double,
        delivery: Double,
        total: Double,
        status: String,
        timestamp: Long
    ) {
        val currentUserEmail = auth.currentUser?.email

        if (currentUserEmail == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val orderData = hashMapOf(
            "subTotal" to subTotal,
            "tax" to tax,
            "delivery" to delivery,
            "total" to total,
            "status" to "Confirmed",
            "timestamp" to timestamp,
            "items" to cartItems?.map { cartItem ->
                mapOf(
                    "name" to cartItem.name,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity,
                    "subTotal" to cartItem.subTotal
                )
            }
        )

        firestore.collection("users")
            .document(currentUserEmail)
            .collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, OrderConfirmationActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}