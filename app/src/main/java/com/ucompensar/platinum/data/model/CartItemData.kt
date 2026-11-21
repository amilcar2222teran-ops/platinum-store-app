package com.ucompensar.platinum.data.model

data class CartItemData(
    val id: String = "",
    val usuarioId: String = "",
    val productoId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val imagenUrl: String = "",
    val fecha: Long = System.currentTimeMillis()
)
