package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme // Importujemy domyślny motyw Material3
import pl.lab2.subtrack.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Używamy domyślnego MaterialTheme zamiast Twojego customowego
            MaterialTheme {
                MainScreen()
            }
        }
    }
}