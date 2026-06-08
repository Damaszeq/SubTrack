package pl.lab2.subtrack

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import pl.lab2.subtrack.ui.AppThemeMode
import pl.lab2.subtrack.ui.AppLanguage
import java.util.Locale

@Composable
fun LocalizationWrapper(
    currentLanguage: AppLanguage,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Wymuszamy aktualizację konfiguracji przy każdej zmianie języka
    val locale = Locale(currentLanguage.code)
    Locale.setDefault(locale)
    configuration.setLocale(locale)

    val resources = context.resources
    resources.updateConfiguration(configuration, resources.displayMetrics)

    key(currentLanguage) {
        content()
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentTheme by viewModel.themeMode.collectAsState()
            val currentLanguage by viewModel.language.collectAsState()

            SubTrackTheme(themeMode = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocalizationWrapper(currentLanguage = currentLanguage) {
                        AppNavigation(subViewModel = viewModel)
                    }
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

        // 5. EKRAN USTAWIEŃ
        composable("settings") {
            SettingsScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}