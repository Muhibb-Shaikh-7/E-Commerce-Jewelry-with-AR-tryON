package com.example.majorproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.adapters.ItemAdapter
import com.example.majorproject.dataClass.item


class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      val view= inflater.inflate(R.layout.fragment_product, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

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