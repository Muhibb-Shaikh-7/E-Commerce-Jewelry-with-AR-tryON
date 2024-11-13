package com.example.majorproject

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentsClient: PaymentsClient
    private var totalAmount: Double = 0.0 // Variable to hold the dynamic total amount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Retrieve the total amount passed from CartActivity
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        // Initialize the Google Pay client
        paymentsClient = Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Use ENVIRONMENT_PRODUCTION for live
                .build()
        )

        // Set up the button to initiate Google Pay
        val payButton: Button = findViewById(R.id.pay_button)
        payButton.setOnClickListener {
            startGooglePay()
        }
    }

    private fun startGooglePay() {
        val paymentDataRequestJson = createPaymentDataRequest()
        paymentDataRequestJson?.let {
            val request = PaymentDataRequest.fromJson(it.toString())
            paymentsClient.loadPaymentData(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val paymentData = task.result
                    handlePaymentSuccess(paymentData)
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e("PaymentActivity", "Payment failed: ${exception.statusCode}")
                        Toast.makeText(this, "Payment failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    } else {
                        // Handle other types of exceptions (e.g., network errors)
                        Log.e("PaymentActivity", "Payment failed: ${exception?.message}")
                        Toast.makeText(this, "Payment failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun createPaymentDataRequest(): JSONObject? {
        val paymentDataRequest = JSONObject()
        try {
            paymentDataRequest.put("apiVersion", 2)
            paymentDataRequest.put("apiVersionMinor", 0)

            // Configure allowed payment methods
            val allowedPaymentMethods = JSONObject()
            allowedPaymentMethods.put("type", "CARD")
            val parameters = JSONObject()
            parameters.put("allowedAuthMethods", JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
            parameters.put("allowedCardNetworks", JSONArray().put("MASTERCARD").put("VISA"))
            allowedPaymentMethods.put("parameters", parameters)

            // Payment information
            val transactionInfo = JSONObject()
            transactionInfo.put("totalPriceStatus", "FINAL")
            transactionInfo.put("totalPrice", totalAmount.toString())  // Use the dynamic amount
            transactionInfo.put("currencyCode", "INR")

            // Merchant information
            val merchantInfo = JSONObject()
            merchantInfo.put("merchantName", "Example Merchant")

            paymentDataRequest.put("allowedPaymentMethods", JSONArray().put(allowedPaymentMethods))
            paymentDataRequest.put("transactionInfo", transactionInfo)
            paymentDataRequest.put("merchantInfo", merchantInfo)

            return paymentDataRequest

        } catch (e: JSONException) {
            Log.e("PaymentActivity", "Failed to create payment data request: ${e.message}")
        }
        return null
    }

    private fun handlePaymentSuccess(paymentData: PaymentData?) {
        paymentData?.let {
            val paymentInformation = paymentData.toJson()
            Log.i("PaymentActivity", "Payment successful: $paymentInformation")
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show()
        }
    }
}
