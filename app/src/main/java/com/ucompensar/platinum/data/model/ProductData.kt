package com.ucompensar.platinum.data.model

data class ProductData(
    val id: String = "",
    val nombre: String = "",
    val tag: String = "",
    val sizes: String = "",
    val descripcion: String = "",
    val materiales: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val imagenUrl: String = ""
)
