package com.example.majorproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.majorproject.R
import com.example.majorproject.dataClass.item
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

// Adapter class for displaying a list of items
class ItemAdapter(
    private val context: Context,
    private val list: List<item>,  // Ensures compatibility with the type `List<com.example.majorproject.dataClass.item>`
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ItemAdapter.ItemAdapterViewHolder>() {

    // Interface to handle click events
    interface OnItemClickListener {
        fun onItemClick(product: item)
    }

    // ViewHolder class to hold com.example.majorproject.dataClass.item views
    inner class ItemAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.isearchmageView)
        val nameView: TextView = view.findViewById(R.id.searchtitleTextView)
        val priceView: TextView = view.findViewById(R.id.priceTextView)
        val styleText: TextView = view.findViewById(R.id.product_style)
        val containerLayout: RelativeLayout = view.findViewById(R.id.containerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapterViewHolder {
        // Inflate the layout for each com.example.majorproject.dataClass.item and return the ViewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ItemAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size // Return the size of the com.example.majorproject.dataClass.item list
    }

    override fun onBindViewHolder(holder: ItemAdapterViewHolder, position: Int) {
        val currentItem = list[position]

        // Check if the image URL is empty or null
        if (!currentItem.image.isNullOrEmpty()) {
            // Load the image using Picasso if the URL is valid
            Picasso.get()
                .load(currentItem.image)
                 // Set a placeholder image
                .into(holder.imageView)
        } else {
            // Load a default placeholder image if the URL is empty or null
            Picasso.get()
                .load(R.drawable.ring1) // Replace with your actual placeholder resource
                .into(holder.imageView)
        }

        // Set name, price, and style on text views
        holder.nameView.text = currentItem.name
        holder.priceView.text = currentItem.price
        holder.styleText.text = currentItem.style

        // Set the click listener for the container layout
        holder.containerLayout.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }}
