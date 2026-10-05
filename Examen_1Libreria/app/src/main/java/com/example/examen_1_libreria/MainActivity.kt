package com.example.examen_1_libreria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.examen_1_libreria.ui.LibreriaNavHost
import com.example.examen_1_libreria.ui.theme.Examen_1LibreriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Examen_1LibreriaTheme {
                LibreriaNavHost()
            }
        }
    }
}