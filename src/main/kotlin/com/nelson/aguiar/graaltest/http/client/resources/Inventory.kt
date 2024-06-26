package com.nelson.aguiar.graaltest.http.client.resources

data class Inventory(
    val productId: String,
    val inventoryId: String,
    val location: String,
    val quantity: Long
)
