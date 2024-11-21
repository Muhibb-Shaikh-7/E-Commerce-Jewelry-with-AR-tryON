package com.example.majorproject.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.dataClass.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class CartAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Define the ViewHolder class
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.itemName)
        val productPrice: TextView = view.findViewById(R.id.itemPrice)
        val productQuantity: TextView = view.findViewById(R.id.itemQuantity)
        val image:ImageView=view.findViewById(R.id.itemImage)
        val delete:ImageView=view.findViewById(R.id.deleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.name
        holder.productPrice.text = "RS. ${"%.2f".format(item.price)}"
        holder.productQuantity.text = "Qty: ${item.quantity}"
        Picasso.get().load(item.image).into(holder.image)
        holder.delete.setOnClickListener {
          FirebaseFirestore.getInstance().collection("cart").document(holder.productName.text.toString()).delete()
        }
    }

    override fun getItemCount(): Int = cartItems.size

    // Method to retrieve the current list of cart items
    fun getCartItems(): List<CartItem> {
        return cartItems
    }
}
