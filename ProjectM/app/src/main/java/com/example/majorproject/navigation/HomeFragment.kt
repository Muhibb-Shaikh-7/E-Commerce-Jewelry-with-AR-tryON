package com.example.majorproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.dataClass.item
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majorproject.adapters.ItemAdapter



class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find RecyclerView and set up the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // Create a list of items
        val itemList = listOf(
            item(R.drawable.necklace1, "Necklace 1", "₹ 38,516"),
            item(R.drawable.necklace2, "Necklace 2", "₹ 38,516"),
            item(R.drawable.necklace3, "Necklace 3", "₹ 38,516"),
            item(R.drawable.necklace4, "Necklace 4", "₹ 38,516"),
            item(R.drawable.neckalce5, "Necklace 5", "₹ 38,516"),
            item(R.drawable.ring1, "Ring 1", "₹ 38,516"),
            item(R.drawable.ring2, "Ring 2", "₹ 38,516"),
            item(R.drawable.ring3, "Ring 3", "₹ 38,516"),
            item(R.drawable.ring4, "Ring 4", "₹ 38,516")
        )

        // Set up the RecyclerView with a LinearLayoutManager and the adapter

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ItemAdapter(requireContext(), itemList)

        return view
    }
}