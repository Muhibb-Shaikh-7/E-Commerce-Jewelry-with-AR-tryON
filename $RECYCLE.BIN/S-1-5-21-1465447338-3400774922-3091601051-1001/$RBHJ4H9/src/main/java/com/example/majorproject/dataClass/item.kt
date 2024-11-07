package com.example.majorproject.dataClass

import com.example.majorproject.dataClass.Product

data class item(
    val image: String = "",
    val name: String = "",
    val price: String = "",
    val style: String = "",
    val product: Product? = null  // Store product object inside com.example.majorproject.dataClass.item
)
