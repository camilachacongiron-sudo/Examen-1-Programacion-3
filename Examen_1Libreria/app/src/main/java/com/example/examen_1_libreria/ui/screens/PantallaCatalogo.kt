package com.example.examen_1_libreria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.examen_1_libreria.catalogo.CategoriaLibro
import com.example.examen_1_libreria.catalogo.Libro
import com.example.examen_1_libreria.ui.LibreriaViewModel
import com.example.examen_1_libreria.ui.components.BotonCarrito
import com.example.examen_1_libreria.ui.components.EtiquetaStock
import com.example.examen_1_libreria.ui.components.PortadaLibro
import com.example.examen_1_libreria.ui.components.ResumenCarritoDialog
import com.example.examen_1_libreria.ui.components.comoBs
import com.example.examen_1_libreria.ui.components.etiqueta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    vm: LibreriaViewModel,
    onLibroClick: (String) -> Unit
) {
    val libros by vm.libros.collectAsState()
    val carrito by vm.estadoCarrito.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<CategoriaLibro?>(null) }
    var mostrarCarrito by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // "libros" va como clave para recalcular cuando cambie el stock tras una compra
    val visibles = remember(libros, busqueda, categoria) { vm.filtrar(busqueda, categoria) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Librería") },
                actions = { BotonCarrito(carrito.unidades) { mostrarCarrito = true } }
            )
        },
        bottomBar = {
            if (carrito.unidades > 0) {
                BottomAppBar {
                    val palabra = if (carrito.unidades == 1) "libro" else "libros"
                    Text(
                        text = "${carrito.unidades} $palabra · ${carrito.total.comoBs()}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                    Button(onClick = { mostrarCarrito = true }) { Text("Ver carrito") }
                    Spacer(Modifier.width(16.dp))
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar título o autor") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = categoria == null,
                        onClick = { categoria = null },
                        label = { Text("Todos") }
                    )
                }
                items(CategoriaLibro.entries) { cat ->
                    FilterChip(
                        selected = categoria == cat,
                        onClick = { categoria = cat },
                        label = { Text(cat.etiqueta()) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (visibles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin resultados")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(visibles, key = { it.id }) { libro ->
                        TarjetaLibro(libro = libro, onClick = { onLibroClick(libro.id) })
                    }
                }
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
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (ok) "Compra realizada" else "No se pudo completar la compra"
                    )
                }
            }
        )
    }
}

/** ElevatedCard + portada + título, autor, precio + etiqueta de stock */
@Composable
private fun TarjetaLibro(libro: Libro, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column {
            PortadaLibro(
                titulo = libro.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )
            Column(Modifier.padding(10.dp)) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = libro.autor,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = libro.precio.comoBs(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                EtiquetaStock(libro.stockDisponible, Modifier.padding(top = 4.dp))
            }
        }
    }
}
