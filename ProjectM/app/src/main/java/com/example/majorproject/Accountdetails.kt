package com.example.majorproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_account_details.*

class Accountdetails : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Fetch user data
        fetchUserData("shaikhsaniya1001@gmail.com") // Replace with current user's email
    }

    private fun fetchUserData(email: String) {
        firestore.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Extract data from document
                    val name = document.getString("name")
                    val address = document.getString("building")
                    val contactNumber = document.getString("contactNumber")
                    val email = document.getString("email")

                    // Update UI components
                    username_edit_text.setText(name)
                    address_edit_text.setText(address)
                    phone_number_edit_text.setText(contactNumber)
                    email_edit_text.setText(email)
                } else {
                    // Document does not exist
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
                println("Error getting documents: ${exception.localizedMessage}")
            }
    }
}