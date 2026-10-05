package com.example.examen_1_libreria.catalogo

data class Libro(
    val id: String,
    val titulo: String,
    val autor: String,
    val isbn: String,
    var precio: Double,
    val categoria: CategoriaLibro,
    var stockDisponible: Int
) {
    fun hayStockSuficiente(cantidad: Int): Boolean = stockDisponible >= cantidad

    fun reducirStock(cantidad: Int): Boolean {
        if (!hayStockSuficiente(cantidad)) return false
        stockDisponible -= cantidad
        return true
    }

    fun incrementarStock(cantidad: Int) {
        stockDisponible += cantidad
    }
}