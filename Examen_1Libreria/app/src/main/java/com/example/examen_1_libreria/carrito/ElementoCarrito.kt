package com.example.examen_1_libreria.carrito

import com.example.examen_1_libreria.catalogo.Libro

data class ElementoCarrito(
    val libro: Libro,
    var cantidad: Int
) {
    fun calcularSubtotal(): Double = libro.precio * cantidad
}