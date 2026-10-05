package com.example.examen_1_libreria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.examen_1_libreria.catalogo.CategoriaLibro
import com.example.examen_1_libreria.ui.EstadoCarrito

const val UMBRAL_STOCK_BAJO = 5

fun Double.comoBs(): String =
    if (this % 1.0 == 0.0) "Bs ${toInt()}" else "Bs ${"%.2f".format(this)}"

fun CategoriaLibro.etiqueta(): String = when (this) {
    CategoriaLibro.FICCION -> "Ficción"
    CategoriaLibro.NO_FICCION -> "No ficción"
    CategoriaLibro.CIENCIA -> "Ciencia"
    CategoriaLibro.TECNOLOGIA -> "Tecnología"
    CategoriaLibro.INFANTIL -> "Infantil"
    CategoriaLibro.OTROS -> "Otros"
}

/** IconButton + BadgedBox + Badge */
@Composable
fun BotonCarrito(unidades: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        BadgedBox(badge = { if (unidades > 0) Badge { Text(unidades.toString()) } }) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
        }
    }
}

/**
 * Portada: AsyncImage si hay URL; si no, un recuadro con la inicial del título.
 * (Libro no tiene campo de imagen; cuando lo agregues, pasa libro.portadaUrl aquí.)
 */
@Composable
fun PortadaLibro(titulo: String, modifier: Modifier = Modifier, url: String? = null) {
    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = titulo,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = titulo.take(1).uppercase(),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/** Text con fondo: "Agotado" / "Quedan N" (no se muestra si hay stock alto). */
@Composable
fun EtiquetaStock(stock: Int, modifier: Modifier = Modifier) {
    val (texto, fondo, color) = when {
        stock <= 0 -> Triple(
            "Agotado",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        stock <= UMBRAL_STOCK_BAJO -> Triple(
            "Quedan $stock",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        else -> return
    }
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
            .background(fondo, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

/** Resumen del carrito (el diseño no incluye pantalla de carrito). */
@Composable
fun ResumenCarritoDialog(
    estado: EstadoCarrito,
    onCerrar: () -> Unit,
    onVaciar: () -> Unit,
    onPagar: () -> Unit
) {
    val vacio = estado.items.isEmpty()
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Tu carrito") },
        text = {
            if (vacio) {
                Text("El carrito está vacío")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    estado.items.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.cantidad} × ${item.libro.titulo}", Modifier.weight(1f))
                            Text(item.calcularSubtotal().comoBs())
                        }
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(estado.total.comoBs(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onPagar, enabled = !vacio) { Text("Pagar") } },
        dismissButton = {
            Row {
                TextButton(onClick = onVaciar, enabled = !vacio) { Text("Vaciar") }
                TextButton(onClick = onCerrar) { Text("Cerrar") }
            }
        }
    )
}
