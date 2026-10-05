package com.example.examen_1_libreria.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.examen_1_libreria.ui.screens.PantallaCatalogo
import com.example.examen_1_libreria.ui.screens.PantallaDetalle

object Rutas {
    const val CATALOGO = "catalogo"
    const val DETALLE = "detalle/{libroId}"
    fun detalle(libroId: String) = "detalle/$libroId"
}

@Composable
fun LibreriaNavHost(
    // viewModel() dentro de setContent queda atado a la Activity: lo comparten ambas pantallas
    vm: LibreriaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Rutas.CATALOGO) {
        composable(Rutas.CATALOGO) {
            PantallaCatalogo(
                vm = vm,
                onLibroClick = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("libroId") { type = NavType.StringType })
        ) { entrada ->
            PantallaDetalle(
                libroId = entrada.arguments?.getString("libroId").orEmpty(),
                vm = vm,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
