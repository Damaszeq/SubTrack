package pl.lab2.subtrack

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.SubscriptionDetailsScreen
import pl.lab2.subtrack.ui.NotificationsScreen
import pl.lab2.subtrack.ui.SettingsScreen // Zaimportuj swój nowy ekran ustawień
import pl.lab2.subtrack.ui.SubscriptionViewModel

@Composable
fun AppNavigation(subViewModel: SubscriptionViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // 1. EKRAN GŁÓWNY
// ... wewnątrz NavHost w MainActivity.kt lub osobnym pliku nawigacji:
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
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}