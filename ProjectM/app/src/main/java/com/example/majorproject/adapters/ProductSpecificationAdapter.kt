package com.example.majorproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.dataClass.ProductSpecification
import com.example.majorproject.databinding.ItemProductSpecificationBinding

class ProductSpecificationAdapter(private val specList: List<ProductSpecification>) :
    RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductSpecificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(spec: ProductSpecification) {
            binding.labelText.text = spec.label
            binding.valueText.text = spec.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductSpecificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spec = specList[position]
        holder.bind(spec)
    }

    override fun getItemCount(): Int = specList.size
}
