package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.SubscriptionDetailsScreen
import pl.lab2.subtrack.ui.NotificationsScreen
import pl.lab2.subtrack.ui.SettingsScreen
import pl.lab2.subtrack.ui.SubscriptionViewModel
import pl.lab2.subtrack.ui.SubTrackTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pl.lab2.subtrack.ui.AppThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val viewModel: SubscriptionViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            SubTrackTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(subViewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(subViewModel: SubscriptionViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // 1. EKRAN GŁÓWNY
        composable("main") {
            MainScreen(
                viewModel = subViewModel,
                onAddClick = { navController.navigate("add") },
                onDetailsClick = { subId -> navController.navigate("details/$subId") },
                onNotificationsClick = { navController.navigate("notifications") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        // 2. EKRAN DODAWANIA
        composable("add") {
            AddSubscriptionScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. EKRAN SZCZEGÓŁÓW
        composable("details/{subId}") { backStackEntry ->
            val subId = backStackEntry.arguments?.getString("subId")
            SubscriptionDetailsScreen(
                subId = subId,
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 4. EKRAN POWIADOMIEŃ
        composable("notifications") {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. NOWY EKRAN USTAWIEŃ
        composable("settings") {
            SettingsScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}