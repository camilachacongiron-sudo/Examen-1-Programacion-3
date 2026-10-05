package com.example.examen_1_libreria.servicio

import com.example.examen_1_libreria.carrito.Carrito
import com.example.examen_1_libreria.catalogo.GestorInventario
import com.example.examen_1_libreria.transacciones.*
import java.time.LocalDateTime
import java.util.UUID

class ProcesadorDeVenta(val gestorInventario: GestorInventario) {

    fun completarVenta(carrito: Carrito, metodoPago: MetodoPago): Pedido? {
        if (carrito.items.isEmpty() || !validarDisponibilidad(carrito)) return null

        val total = carrito.calcularTotal()
        val pago = Pago(UUID.randomUUID().toString(), total, metodoPago, LocalDateTime.now())
        if (!pago.procesarPago()) return null

        carrito.items.forEach { it.libro.reducirStock(it.cantidad) }

        val detalles = carrito.items.map {
            DetalleVenta(it.libro.id, it.libro.titulo, it.libro.precio, it.cantidad)
        }

        val pedido = Pedido(
            id = UUID.randomUUID().toString(),
            cliente = carrito.cliente,
            items = detalles,
            pago = pago,
            total = total,
            fecha = LocalDateTime.now(),
            estado = EstadoPedido.COMPLETADO
        )

        carrito.vaciar()
        return pedido
    }

    fun validarDisponibilidad(carrito: Carrito): Boolean =
        carrito.items.all { it.libro.hayStockSuficiente(it.cantidad) }
}