package pl.lab2.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.NotificationsScreen
import pl.lab2.subtrack.ui.SubTrackTheme
import pl.lab2.subtrack.ui.SubscriptionDetailsScreen
import pl.lab2.subtrack.ui.SubscriptionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubTrackTheme {
                AppNavigation()
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
        composable("main") {
            MainScreen(
                viewModel = subViewModel,
                onAddClick = { navController.navigate("add") },
                onDetailsClick = { subId -> navController.navigate("details/$subId") },
                onNotificationsClick = { navController.navigate("notifications") }
            )
        }

        composable("add") {
            AddSubscriptionScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("details/{subId}") { backStackEntry ->
            val subId = backStackEntry.arguments?.getString("subId")
            SubscriptionDetailsScreen(
                subId = subId,
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}