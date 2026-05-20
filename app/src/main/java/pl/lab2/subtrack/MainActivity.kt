package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.MainScreen
import kotlinx.serialization.Serializable
import pl.lab2.subtrack.ui.SubTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubTrackTheme { // Tutaj aplikacja startuje z poprawnym motywem
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Tradycyjne, niezawodne podejście oparte na ciągach tekstowych (String)
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // Ekran główny
        composable("main") {
            MainScreen(
                onAddClick = {
                    navController.navigate("add")
                }
            )
        }

        // Ekran dodawania subskrypcji
        composable("add") {
            AddSubscriptionScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}