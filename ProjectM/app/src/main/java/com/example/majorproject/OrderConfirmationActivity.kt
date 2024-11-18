import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var orderId: String
    private lateinit var trackingId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        db = FirebaseFirestore.getInstance()
        orderId = db.collection("orders").document().id // Auto-generate a unique orderId
        trackingId = generateTrackingId() // Generate tracking ID

        val btnSaveTrackingId = findViewById<Button>(R.id.btnSaveTrackingId)
        val etTrackingId = findViewById<EditText>(R.id.etTrackingId)
        val tvOrderId = findViewById<TextView>(R.id.tvOrderId)
        val tvOrderDetails = findViewById<TextView>(R.id.tvOrderDetailsHeader)

        // Display generated order ID
        tvOrderId.text = "Order ID: #$orderId"
        etTrackingId.setText(trackingId)

        // Fetch order details from Firestore and display them
        fetchOrderDetails(orderId, tvOrderDetails)

        // Save Tracking ID to Firestore
        btnSaveTrackingId.setOnClickListener {
            val inputTrackingId = etTrackingId.text.toString()
            if (inputTrackingId.isNotEmpty()) {
                saveTrackingIdToFirestore(inputTrackingId)
            } else {
                Toast.makeText(this, "Please enter a tracking ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateTrackingId(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..8).map { chars.random() }.joinToString("")
    }

    private fun saveTrackingIdToFirestore(trackingId: String) {
        val orderData = hashMapOf(
            "orderId" to orderId,
            "trackingId" to trackingId,
            "status" to "Processing",
            "totalPrice" to 550.0
        )

        db.collection("orders").document(orderId)
            .set(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Tracking ID saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("OrderConfirmation", "Error saving tracking ID", e)
            }
    }

    private fun fetchOrderDetails(orderId: String, tvOrderDetails: TextView) {
        db.collection("orders").document(orderId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val details = StringBuilder()
                    details.append("Item: ").append(document.getString("itemName")).append("\n")
                    details.append("Quantity: ").append(document.getLong("quantity")).append("\n")
                    details.append("Price: $").append(document.getDouble("totalPrice")).append("\n")
                    tvOrderDetails.text = details.toString()
                } else {
                    tvOrderDetails.text = "Order details not found."
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrderConfirmation", "Error fetching order details", e)
                Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show()
            }
    }
}
