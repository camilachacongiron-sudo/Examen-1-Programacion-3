package com.example.examen_1_libreria.usuario

class Cliente(
    id: String,
    nombre: String,
    email: String,
    var direccionEnvio: String,
    val telefono: String
) : Usuario(id, nombre, email) {

    fun actualizarDireccion(nuevaDireccion: String) {
        direccionEnvio = nuevaDireccion
    }
}