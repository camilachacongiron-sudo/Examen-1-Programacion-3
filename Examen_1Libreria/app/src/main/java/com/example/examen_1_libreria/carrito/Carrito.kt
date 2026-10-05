package com.example.examen_1_libreria.carrito


import com.example.examen_1_libreria.catalogo.Libro
import com.example.examen_1_libreria.usuario.Cliente

class Carrito(val cliente: Cliente) {
    val items: MutableList<ElementoCarrito> = mutableListOf()

    fun agregarItem(libro: Libro, cantidad: Int) {
        val existente = items.find { it.libro.id == libro.id }
        if (existente != null) {
            existente.cantidad += cantidad
        } else {
            items.add(ElementoCarrito(libro, cantidad))
        }
    }

    fun modificarCantidad(libroId: String, nuevaCantidad: Int) {
        items.find { it.libro.id == libroId }?.cantidad = nuevaCantidad
    }

    fun removerItem(libroId: String) {
        items.removeAll { it.libro.id == libroId }
    }

    fun calcularTotal(): Double = items.sumOf { it.calcularSubtotal() }

    fun vaciar() {
        items.clear()
    }
}