package com.example.examen_1_libreria.transacciones


import com.example.examen_1_libreria.usuario.Cliente
import java.time.LocalDateTime

data class Pedido(
    val id: String,
    val cliente: Cliente,
    val items: List<DetalleVenta>,
    val pago: Pago,
    val total: Double,
    val fecha: LocalDateTime,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE
) {
    fun generarResumen(): String {
        val detalle = items.joinToString("\n") {
            "- ${it.tituloLibro} x${it.cantidad} = ${it.calcularSubtotal()}"
        }
        return "Pedido $id | Cliente: ${cliente.nombre} | Estado: $estado\n$detalle\nTotal: $total"
    }

    fun cancelarPedido() {
        estado = EstadoPedido.CANCELADO
    }
}