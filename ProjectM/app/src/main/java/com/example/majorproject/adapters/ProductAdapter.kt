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

class ProductAdapter(val context: Context, val list: List<item>) :
    RecyclerView.Adapter<ProductAdapter.ItemAdapterViewModel>() {

    class ItemAdapterViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.isearchmageView)
        val nameView: TextView = view.findViewById(R.id.searchtitleTextView)
        val weightView: TextView = view.findViewById(R.id.priceTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapterViewModel {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ItemAdapterViewModel(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemAdapterViewModel, position: Int) {
        val currentItem = list[position]
        Glide.with(context)
            .load(currentItem.image) // Use the URL instead of resource ID
            .into(holder.imageView)
        holder.nameView.text = currentItem.name
        holder.weightView.text = currentItem.price

    }
}