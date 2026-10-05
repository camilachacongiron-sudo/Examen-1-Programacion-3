package com.example.examen_1_libreria.transacciones


data class DetalleVenta(
    val libroId: String,
    val tituloLibro: String,
    val precioUnitario: Double,
    val cantidad: Int
) {
    fun calcularSubtotal(): Double = precioUnitario * cantidad
}