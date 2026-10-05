package com.example.examen_1_libreria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.examen_1_libreria.ui.LibreriaViewModel
import com.example.examen_1_libreria.ui.components.BotonCarrito
import com.example.examen_1_libreria.ui.components.PortadaLibro
import com.example.examen_1_libreria.ui.components.ResumenCarritoDialog
import com.example.examen_1_libreria.ui.components.comoBs
import com.example.examen_1_libreria.ui.components.etiqueta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    libroId: String,
    vm: LibreriaViewModel,
    onVolver: () -> Unit
) {
    val libros by vm.libros.collectAsState()
    val carrito by vm.estadoCarrito.collectAsState()

    // "libros" va como clave: al cambiar el stock se vuelve a leer el libro
    val libro = remember(libros, libroId) { vm.obtenerLibroPorId(libroId) }

    var cantidad by remember { mutableIntStateOf(1) }
    var mostrarCarrito by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun avisar(mensaje: String) {
        scope.launch { snackbarHostState.showSnackbar(mensaje) }
    }

    // Tope por stock: lo que queda menos lo que ya está en el carrito
    val enCarrito = carrito.items.find { it.libro.id == libroId }?.cantidad ?: 0
    val tope = ((libro?.stockDisponible ?: 0) - enCarrito).coerceAtLeast(0)
    val cantidadOk = cantidad.coerceIn(1, maxOf(tope, 1))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = { BotonCarrito(carrito.unidades) { mostrarCarrito = true } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (libro != null) {
                BottomAppBar {
                    Button(
                        onClick = {
                            if (vm.agregarAlCarrito(libro, cantidadOk)) {
                                cantidad = 1
                                avisar("Agregado al carrito")
                            }
                        },
                        enabled = tope > 0,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) { Text("Agregar al carrito") }

                    OutlinedButton(
                        onClick = {
                            if (vm.comprarAhora(libro, cantidadOk)) {
                                cantidad = 1
                                avisar("Compra realizada")
                            } else {
                                avisar("No se pudo completar la compra")
                            }
                        },
                        enabled = tope > 0,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) { Text("Comprar ahora") }
                }
            }
        }
    ) { innerPadding ->
        if (libro == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { Text("Libro no encontrado") }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PortadaLibro(
                    titulo = libro.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                )

                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${libro.autor} · ISBN ${libro.isbn}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(libro.categoria.etiqueta()) })
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                if (libro.stockDisponible <= 0) "Agotado"
                                else "Quedan ${libro.stockDisponible}"
                            )
                        }
                    )
                }

                Text(
                    text = libro.precio.comoBs(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Cantidad: Row + IconButton + Text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { cantidad = cantidadOk - 1 },
                        enabled = cantidadOk > 1
                    ) { Text("-", style = MaterialTheme.typography.titleLarge) }
                    Text(
                        text = cantidadOk.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(min = 40.dp)
                    )
                    IconButton(
                        onClick = { cantidad = cantidadOk + 1 },
                        enabled = cantidadOk < tope
                    ) { Text("+", style = MaterialTheme.typography.titleLarge) }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (mostrarCarrito) {
        ResumenCarritoDialog(
            estado = carrito,
            onCerrar = { mostrarCarrito = false },
            onVaciar = {
                vm.vaciarCarrito()
                mostrarCarrito = false
            },
            onPagar = {
                val ok = vm.finalizarCompra()
                mostrarCarrito = false
                avisar(if (ok) "Compra realizada" else "No se pudo completar la compra")
            }
        )
    }
}
