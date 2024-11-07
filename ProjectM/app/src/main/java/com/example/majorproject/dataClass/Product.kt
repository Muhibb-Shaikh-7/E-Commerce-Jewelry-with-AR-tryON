package com.example.majorproject.dataClass

import java.io.Serializable

data class Product(
    val name: String = "",
    val price: String = "",
    val images: Map<String, String> = emptyMap(),  // Map<String, String> for images
    val grossWeight: Map<String, Any> = emptyMap(),  // Map<String, String> for gross weight
    val priceBreaking: Map<String, String> = emptyMap(),  // Map<String, String> for price breakdown
    val productSpecification: Map<String, String> = emptyMap(),  // Map<String, String> for product specs
    val size: Map<String, String> = emptyMap(),  // Map<String, String> for size
    val stock: String = "",
    val styling: Map<String, String> = emptyMap()  // Map<String, String> for styling
) : Serializable
