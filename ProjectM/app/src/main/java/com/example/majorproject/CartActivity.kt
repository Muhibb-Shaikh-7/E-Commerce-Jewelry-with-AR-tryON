package com.example.majorproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import CartAdapter
import CartItem
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore


class CartActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var deliveryTextView: TextView
    private lateinit var subTotalTextView: TextView
    private lateinit var imgView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        firestore = Firebase.firestore

        cartRecyclerView = findViewById(R.id.cartRecyclerView) // recycler view id
        totalTextView = findViewById(R.id.textView4)
        taxTextView = findViewById(R.id.textView8)
        deliveryTextView = findViewById(R.id.textView9)
        subTotalTextView = findViewById(R.id.textView10)
        imgView = findViewById(R.id.back)

        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCartItems()

        imgView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: close the current activity to remove it from the back stack
        }

    }


    private fun loadCartItems() {
        firestore.collection("cartItems")
            .get()
            .addOnSuccessListener { result ->
                var total = 0.0
                val cartItems = mutableListOf<CartItem>()

                for (document in result) {
                    val item = document.toObject(CartItem::class.java)
                    cartItems.add(item)

                    total += item.subTotal ?: 0.0
                }

                cartRecyclerView.adapter = CartAdapter(cartItems)

                val tax = total * 18 // Assuming 8% tax rate
                val delivery = 10.0 // Flat delivery fee

                subTotalTextView.text = "RS. ${total}"
                taxTextView.text = "RS. ${tax}"
                deliveryTextView.text = "RS. ${delivery}"
                totalTextView.text = "RS. ${total + tax + delivery}"
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to load cart items: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}