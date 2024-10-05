package com.example.majorproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.majorproject.R
import com.example.majorproject.dataClass.item
import com.google.android.material.imageview.ShapeableImageView

class ItemAdapter(private val context: Context, private val list: List<item>) :
    RecyclerView.Adapter<ItemAdapter.ItemAdapterViewModel>() {

    // ViewHolder class to hold item views
    class ItemAdapterViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.isearchmageView)
        val nameView: TextView = view.findViewById(R.id.searchtitleTextView)
        val priceView: TextView = view.findViewById(R.id.priceTextView)
        val styleText: TextView = view.findViewById(R.id.product_style)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapterViewModel {
        // Inflate the layout for each item
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ItemAdapterViewModel(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemAdapterViewModel, position: Int) {
        // Get the current item from the list
        val currentItem = list[position]

        // Load the image using Glide
        Glide.with(context)
            .load(currentItem.image)   // Load image URL
            .into(holder.imageView)

        // Set name and price text views
        holder.nameView.text = currentItem.name
        holder.priceView.text = currentItem.price
        holder.styleText.text = currentItem.style
    }
}
