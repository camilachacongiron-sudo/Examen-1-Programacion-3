package com.example.examen_1_libreria.usuario

import com.example.examen_1_libreria.catalogo.Libro

class Vendedor(
    id: String,
    nombre: String,
    email: String,
    val codigoEmpleado: String
) : Usuario(id, nombre, email) {

    fun modificarStock(libro: Libro, nuevoStock: Int) {
        libro.stockDisponible = nuevoStock
    }
}