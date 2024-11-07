package com.example.majorproject

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneNumberEditText: EditText

    private lateinit var backButton: ImageView
    private lateinit var editProfileImageView: ImageView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        /* Initialize UI components */
        usernameEditText = findViewById(R.id.username_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        addressEditText = findViewById(R.id.address_edit_text)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)

        backButton = findViewById(R.id.back_button1)
        editProfileImageView = findViewById(R.id.editprfile_imgView)

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email?.replace(".", ",") ?: ""
            loadUserData(userEmail)
        } else {
            // Show toast message if user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Click listener for back button to navigate back to ProfileFragment
        backButton.setOnClickListener {
            // Finish the activity to return to the previous screen
            finish()
        }

        // Click listener for edit profile button to navigate to EditProfileActivity
        editProfileImageView.setOnClickListener {
            val intent = Intent(this, Editprofile::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserData(userId: String) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val address = snapshot.child("building").value.toString() +
                            ", " + snapshot.child("city").value.toString() +
                            ", " + snapshot.child("state").value.toString() +
                            ", " + snapshot.child("pincode").value.toString()
                    val phoneNumber = snapshot.child("contactNumber").value.toString()

                    // Populate EditText fields with user data
                    usernameEditText.setText(name)
                    emailEditText.setText(email)
                    addressEditText.setText(address)
                    phoneNumberEditText.setText(phoneNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors (e.g., show a message to the user)
            }
        })
    }
}
