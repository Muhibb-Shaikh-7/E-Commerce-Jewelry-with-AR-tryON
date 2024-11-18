package com.example.majorproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {


    companion object {
        const val UPI_PAYMENT_REQUEST_CODE = 123
        const val UPI_ID = "mohdirfanulhaque23106@okicici"
        const val MERCHANT_NAME = "Mahvir gems"
        const val TRANSACTION_NOTE = "Transaction for Order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val totalAmount:Double = intent.getDoubleExtra("TOTAL_AMOUNT",1.00)
        val payButton: Button = findViewById(R.id.pay_button)
        payButton.setOnClickListener {
            initiateUpiPayment(totalAmount) // Replace with your dynamic amount
        }
    }

    private fun initiateUpiPayment(amount: Double) {
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", UPI_ID) // Payee VPA (UPI ID)
            .appendQueryParameter("pn", MERCHANT_NAME) // Payee name
            .appendQueryParameter("tn", TRANSACTION_NOTE) // Transaction note
            .appendQueryParameter("am", amount.toString()) // Amount
            .appendQueryParameter("cu", "INR") // Currency
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
                if (data != null) {
                    val response = data.getStringExtra("response")
                    processUpiResponse(response)
                } else {
                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processUpiResponse(response: String?) {
        if (response != null && response.contains("SUCCESS", true)) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
        }
    }
}