package pl.lab2.subtrack

import android.R.attr.type
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import pl.lab2.subtrack.ui.MainScreen
import pl.lab2.subtrack.ui.AddSubscriptionScreen
import pl.lab2.subtrack.ui.SubscriptionDetailsScreen
import pl.lab2.subtrack.ui.NotificationsScreen
import pl.lab2.subtrack.ui.SettingsScreen
import pl.lab2.subtrack.ui.SubscriptionViewModel
import pl.lab2.subtrack.ui.NotificationViewModel
import pl.lab2.subtrack.ui.SubTrackTheme
import pl.lab2.subtrack.ui.AppThemeMode
import pl.lab2.subtrack.ui.AppLanguage
import pl.lab2.subtrack.ui.EditSubscriptionScreen
import pl.lab2.subtrack.notification.NotificationScheduler
import pl.lab2.subtrack.ui.AppViewModelProvider
import pl.lab2.subtrack.ui.FinancialStatsScreen
import pl.lab2.subtrack.ui.ArchiveScreen
import java.util.Locale

@Composable
fun LocalizationWrapper(
    currentLanguage: AppLanguage,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val locale = Locale(currentLanguage.code)
    Locale.setDefault(locale)
    configuration.setLocale(locale)

    val resources = context.resources
    resources.updateConfiguration(configuration, resources.displayMetrics)

    content()
}

class MainActivity : ComponentActivity() {
    private val subscriptionViewModel: SubscriptionViewModel by viewModels { AppViewModelProvider.Factory }

    // NAPRAWIONE: Dodano AppViewModelProvider.Factory, żeby Android wiedział, jak przekazać repozytorium do NotificationViewModel
    private val notificationViewModel: NotificationViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SubTrackApplication

        val scheduler = NotificationScheduler(
            context = applicationContext,
            paymentRepository = app.container.paymentRepository,
            notificationHistoryRepository = app.container.notificationHistoryRepository
        )

        scheduler.scheduleDailyNotificationCheck()

        setContent {
            val currentTheme by subscriptionViewModel.themeMode.collectAsState()
            val currentLanguage by subscriptionViewModel.language.collectAsState()

            SubTrackTheme(themeMode = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocalizationWrapper(currentLanguage = currentLanguage) {
                        AppNavigation(
                            subViewModel = subscriptionViewModel,
                            notifViewModel = notificationViewModel,
                            currentLanguage = currentLanguage
                        )
                    }
                }
            }
        }
    }
}

const val ROUTE_STATS = "financial_stats"

@Composable
fun AppNavigation(
    subViewModel: SubscriptionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    notifViewModel: NotificationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentLanguage: AppLanguage
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // 1. EKRAN GŁÓWNY
        composable("main") {
            MainScreen(
                viewModel = subViewModel,
                notifViewModel = notifViewModel,
                onAddClick = { navController.navigate("add") },
                onDetailsClick = { subId -> navController.navigate("details/$subId") },
                onNotificationsClick = { navController.navigate("notifications") },
                onSettingsClick = { navController.navigate("settings") },
                onArchiveClick = { navController.navigate("archive") },
                onTotalSumClick = { navController.navigate(ROUTE_STATS) }
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
        composable(
            route = "details/{subId}",
            exitTransition = {
                fadeOut(animationSpec = tween(250)) +
                        scaleOut(targetScale = 0.95f, animationSpec = tween(250))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.95f, animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val subId = backStackEntry.arguments?.getString("subId") ?: ""
            SubscriptionDetailsScreen(
                subId = subId,
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate("edit_subscription/$id")
                }
            )
        }

        // 4. EKRAN POWIADOMIEŃ
        composable("notifications") {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                notificationViewModel = notifViewModel
            )
        }

        // 5. EKRAN USTAWIEŃ
        composable("settings") {
            androidx.compose.runtime.key(currentLanguage) {
                SettingsScreen(
                    viewModel = subViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // 6. EKRAN EDYCJI
        composable(
            route = "edit_subscription/{subId}",
            arguments = listOf(navArgument("subId") { type = NavType.StringType }),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.95f, animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) +
                        scaleOut(targetScale = 0.95f, animationSpec = tween(250))
            }
        ) { backStackEntry ->
            val subId = backStackEntry.arguments?.getString("subId") ?: ""
            EditSubscriptionScreen(
                subscriptionId = subId,
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 7. EKRAN STATYSTYK
        composable(ROUTE_STATS) {
            FinancialStatsScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 8. EKRAN ARCHIWUM
        composable("archive") {
            ArchiveScreen(
                viewModel = subViewModel,
                onBackClick = { navController.popBackStack() },
                onSubscriptionClick = { subId ->
                    navController.navigate("details/$subId")
                }
            )
        }
    }
}