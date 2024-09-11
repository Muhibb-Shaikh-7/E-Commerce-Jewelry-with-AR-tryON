package com.example.majorproject.dataClass

data class Product(
    val name: String = "",
    val price: String = "",
    val images: Map<String, String> = emptyMap(),
    val grossWeight: Map<String, String> = emptyMap(),
    val priceBreaking: Map<String, String> = emptyMap(),
    val productSpecification: Map<String, String> = emptyMap(),
    val size: Map<String, String> = emptyMap(),
    val stock: String = "",
    val styling: Map<String, String> = emptyMap()
)
