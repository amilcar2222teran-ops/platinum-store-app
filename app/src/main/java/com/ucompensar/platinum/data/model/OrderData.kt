package com.ucompensar.platinum.data.model

data class OrderData(
    val id: String = "",
    val items: List<CartItemData> = emptyList(),
    val direccionEntrega: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val total: Double = 0.0,
    val fecha: Long = System.currentTimeMillis()
)
