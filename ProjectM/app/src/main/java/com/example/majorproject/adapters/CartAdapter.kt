import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.dataClass.CartItem

class CartAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName) // Replace with actual ID
        val itemPrice: TextView = view.findViewById(R.id.itemPrice) // Replace with actual ID
        val itemQuantity: TextView = view.findViewById(R.id.itemQuantity) // Replace with actual ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false) // cart_item.xml for each item in the RecyclerView
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = "$${item.price}"
        holder.itemQuantity.text = "Qty: ${item.quantity}"
    }

    override fun getItemCount(): Int = cartItems.size
}
