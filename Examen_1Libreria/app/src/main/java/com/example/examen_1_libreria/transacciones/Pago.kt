package com.example.examen_1_libreria.transacciones


import java.time.LocalDateTime

data class Pago(
    val id: String,
    val monto: Double,
    val metodo: MetodoPago,
    val fechaHora: LocalDateTime,
    var exitoso: Boolean = false
) {
    fun procesarPago(): Boolean {
        exitoso = monto > 0
        return exitoso
    }
}