package com.example.examen_1_libreria.ui

import androidx.lifecycle.ViewModel
import com.example.examen_1_libreria.carrito.Carrito
import com.example.examen_1_libreria.carrito.ElementoCarrito
import com.example.examen_1_libreria.catalogo.CategoriaLibro
import com.example.examen_1_libreria.catalogo.GestorInventario
import com.example.examen_1_libreria.catalogo.Libro
import com.example.examen_1_libreria.servicio.ProcesadorDeVenta
import com.example.examen_1_libreria.transacciones.MetodoPago
import com.example.examen_1_libreria.usuario.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Foto inmutable del carrito para que Compose detecte los cambios. */
data class EstadoCarrito(
    val items: List<ElementoCarrito> = emptyList(),
    val unidades: Int = 0,
    val total: Double = 0.0
)

class LibreriaViewModel : ViewModel() {

    private val gestor = GestorInventario()
    private val cliente = Cliente("C1", "Cliente Demo", "cliente@demo.com", "La Paz", "70000000")
    private val carrito = Carrito(cliente)
    private val procesador = ProcesadorDeVenta(gestor)

    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros.asStateFlow()

    private val _estadoCarrito = MutableStateFlow(EstadoCarrito())
    val estadoCarrito: StateFlow<EstadoCarrito> = _estadoCarrito.asStateFlow()

    init {
        cargarDatosDeEjemplo()
        publicarCatalogo()
        publicarCarrito()
    }

    // ---------- Consultas (usan GestorInventario) ----------

    fun obtenerLibroPorId(id: String): Libro? = gestor.obtenerLibroPorId(id)

    fun filtrar(consulta: String, categoria: CategoriaLibro?): List<Libro> {
        val base = if (categoria == null) gestor.catalogoLibros.toList()
        else gestor.filtrarPorCategoria(categoria)
        if (consulta.isBlank()) return base
        val ids = (gestor.buscarPorTitulo(consulta) + gestor.buscarPorAutor(consulta))
            .map { it.id }
            .toSet()
        return base.filter { it.id in ids }
    }

    // ---------- Acciones (usan Carrito / ProcesadorDeVenta) ----------

    fun agregarAlCarrito(libro: Libro, cantidad: Int): Boolean {
        val original = gestor.obtenerLibroPorId(libro.id) ?: return false
        if (cantidad <= 0 || cantidad > disponibleParaAgregar(original)) return false
        carrito.agregarItem(original, cantidad)
        publicarCarrito()
        return true
    }

    fun comprarAhora(libro: Libro, cantidad: Int): Boolean {
        val original = gestor.obtenerLibroPorId(libro.id) ?: return false
        if (cantidad <= 0 || cantidad > disponibleParaAgregar(original)) return false
        val temporal = Carrito(cliente).apply { agregarItem(original, cantidad) }
        val pedido = procesador.completarVenta(temporal, MetodoPago.Efectivo)
        publicarCatalogo()
        return pedido != null
    }

    fun finalizarCompra(): Boolean {
        val pedido = procesador.completarVenta(carrito, MetodoPago.Efectivo)
        publicarCatalogo()
        publicarCarrito()
        return pedido != null
    }

    fun vaciarCarrito() {
        carrito.vaciar()
        publicarCarrito()
    }

    // ---------- Internos ----------

    private fun disponibleParaAgregar(libro: Libro): Int {
        val enCarrito = carrito.items.find { it.libro.id == libro.id }?.cantidad ?: 0
        return libro.stockDisponible - enCarrito
    }

    // Se publican COPIAS: los modelos tienen "var" y mutan en sitio,
    // y StateFlow ignora emisiones iguales a la anterior.
    private fun publicarCatalogo() {
        _libros.value = gestor.catalogoLibros.map { it.copy() }
    }

    private fun publicarCarrito() {
        _estadoCarrito.value = EstadoCarrito(
            items = carrito.items.map { it.copy() },
            unidades = carrito.items.sumOf { it.cantidad },
            total = carrito.calcularTotal()
        )
    }

    private fun cargarDatosDeEjemplo() {
        listOf(
            Libro("L01", "Cien años de soledad", "Gabriel García Márquez", "978-0-00-000001-0", 60.0, CategoriaLibro.FICCION, 3),
            Libro("L02", "El Aleph", "Jorge Luis Borges", "978-0-00-000002-0", 45.0, CategoriaLibro.FICCION, 0),
            Libro("L03", "Breve historia del tiempo", "Stephen Hawking", "978-0-00-000003-0", 75.0, CategoriaLibro.CIENCIA, 12),
            Libro("L04", "Cosmos", "Carl Sagan", "978-0-00-000004-0", 50.0, CategoriaLibro.CIENCIA, 8),
            Libro("L05", "Sapiens", "Yuval Noah Harari", "978-0-00-000005-0", 85.0, CategoriaLibro.NO_FICCION, 2),
            Libro("L06", "Clean Code", "Robert C. Martin", "978-0-00-000006-0", 120.0, CategoriaLibro.TECNOLOGIA, 6),
            Libro("L07", "El principito", "Antoine de Saint-Exupéry", "978-0-00-000007-0", 35.0, CategoriaLibro.INFANTIL, 15),
            Libro("L08", "Rayuela", "Julio Cortázar", "978-0-00-000008-0", 55.0, CategoriaLibro.FICCION, 1)
        ).forEach(gestor::registrarLibro)
    }
}
