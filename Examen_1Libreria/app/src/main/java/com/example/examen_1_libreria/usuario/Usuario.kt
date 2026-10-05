package com.example.examen_1_libreria.usuario

abstract class Usuario(
    val id: String,
    val nombre: String,
    val email: String
) {
    fun obtenerPerfil(): String = "[$id] $nombre - $email"
}