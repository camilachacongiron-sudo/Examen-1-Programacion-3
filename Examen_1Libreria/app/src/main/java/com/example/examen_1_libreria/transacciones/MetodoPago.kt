package com.example.examen_1_libreria.transacciones


sealed class MetodoPago {
    object Efectivo : MetodoPago()

    data class Tarjeta(
        val numeroTarjeta: String,
        val titular: String,
        val expiracion: String
    ) : MetodoPago()

    data class TransferenciaQR(val codigoQR: String) : MetodoPago()
}