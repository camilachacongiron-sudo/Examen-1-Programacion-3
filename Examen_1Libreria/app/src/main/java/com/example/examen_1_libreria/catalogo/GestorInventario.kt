package com.example.examen_1_libreria.catalogo

class GestorInventario {
    val catalogoLibros: MutableList<Libro> = mutableListOf()

    fun registrarLibro(libro: Libro) {
        catalogoLibros.add(libro)
    }

    fun actualizarPrecio(libroId: String, nuevoPrecio: Double) {
        obtenerLibroPorId(libroId)?.precio = nuevoPrecio
    }

    fun buscarPorTitulo(titulo: String): List<Libro> =
        catalogoLibros.filter { it.titulo.contains(titulo, ignoreCase = true) }

    fun buscarPorAutor(autor: String): List<Libro> =
        catalogoLibros.filter { it.autor.contains(autor, ignoreCase = true) }

    fun filtrarPorCategoria(categoria: CategoriaLibro): List<Libro> =
        catalogoLibros.filter { it.categoria == categoria }

    fun obtenerLibroPorId(id: String): Libro? =
        catalogoLibros.find { it.id == id }
}