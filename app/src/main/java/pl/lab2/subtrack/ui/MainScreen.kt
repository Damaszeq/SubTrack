package pl.lab2.subtrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.Subscription
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import pl.lab2.subtrack.data.resolvePlanName
import java.util.Calendar

private fun cleanCustomPlanName(rawPlan: String): String {
    if (!rawPlan.contains("|")) return rawPlan
    val nameBeforePipe = rawPlan.substringBefore("|").trim()
    return nameBeforePipe.ifEmpty { "Plan niestandardowy" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SubscriptionViewModel,
    notifViewModel: NotificationViewModel,
    onAddClick: () -> Unit,
    onDetailsClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onArchiveClick: () -> Unit,
    onTotalSumClick: () -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val totalMonthlyCost by viewModel.totalMonthlyCost.collectAsState()
    val hasUnreadNotifications by notifViewModel.hasUnreadNotifications.collectAsState(initial = false)

    var isReadmeVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Track",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isReadmeVisible = !isReadmeVisible }) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Pomoc i instrukcja",
                                tint = if (isReadmeVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(onClick = onArchiveClick) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = "Zobacz archiwum",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (hasUnreadNotifications) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(id = R.string.notifications_title)
                            )
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(id = R.string.settings_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = isReadmeVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    AppReadmeCard(onCloseClick = { isReadmeVisible = false })
                }

                subscriptions.forEach { sub ->
                    SubscriptionItem(
                        subscription = sub,
                        onClick = { onDetailsClick(sub.id.toString()) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable { onTotalSumClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.total_monthly_expenses),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(LocalLocale.current.platformLocale, "%.2f PLN", totalMonthlyCost),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(14.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_subscription)
                    )
                }
            }
        }
    }
}

@Composable
fun AppReadmeCard(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Pomoc",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Witaj w SubTrack!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Zamknij przewodnik",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Twoje centrum kontroli nad subskrypcjami premium. Aplikacja ułatwia codzienne zarządzanie powtarzającymi się płatnościami:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val bulletPoints = listOf(
                "📈 **Statystyki w czasie:** Kliknij w dolny panel z kwotą, aby zobaczyć zaawansowaną prognozę wydatków oraz strukturę kategorii.",
                "🔔 **Powiadomienia:** System automatycznie ostrzeże Cię przed zbliżającym się terminem odnowienia usługi lub końcem okresu próbnego (Trial).",
                "🎨 **Elastyczność:** Dodawaj usługi z gotowych szablonów lub twórz własne, niestandardowe plany za pomocą przycisku +."
            )

            bulletPoints.forEach { point ->
                Text(
                    text = point,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val displayedPlanName = remember(subscription.plan) {
        val cleanPlan = cleanCustomPlanName(subscription.plan)
        if (subscription.plan.contains("|") || subscription.plan.startsWith("custom_")) {
            cleanPlan
        } else {
            resolvePlanName(subscription.plan, context)
        }
    }

    // Określenie, czy subskrypcja zacznie się dopiero w przyszłości
    val isFutureSub = remember(subscription.startDate) {
        subscription.startDate > System.currentTimeMillis()
    }

    // Obliczenie dni pozostałych do rozpoczęcia (lub płatności)
    val daysLeft = remember(subscription.nextPaymentDate, subscription.startDate, isFutureSub) {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetDate = if (isFutureSub) subscription.startDate else subscription.nextPaymentDate

        val subCal = Calendar.getInstance().apply {
            timeInMillis = targetDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffInMs = subCal.timeInMillis - todayCal.timeInMillis
        (diffInMs / (1000 * 60 * 60 * 24)).toInt()
    }

    val dateFormatter = remember { java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()) }
    val formattedTargetDate = dateFormatter.format(
        java.util.Date(if (isFutureSub) subscription.startDate else subscription.nextPaymentDate)
    )

    // Dynamiczna stylizacja kart (Priorytetyzacja: Przyszły start > Okres Próbny > Standard)
    val cardColors = when {
        isFutureSub -> {
            CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f))
        }
        subscription.isTrial -> {
            CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.12f))
        }
        else -> {
            CardDefaults.outlinedCardColors()
        }
    }

    val cardBorder = when {
        isFutureSub -> {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        }
        subscription.isTrial -> {
            BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
        }
        else -> {
            CardDefaults.outlinedCardBorder()
        }
    }

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = cardColors,
        border = cardBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Dodanie plakietek statusu na górze karty
            if (isFutureSub) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.future_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            } else if (subscription.isTrial) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.trial_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SubscriptionIcon(
                    serviceName = subscription.name,
                    planKey = subscription.plan,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = subscription.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = displayedPlanName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%.2f PLN", subscription.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            isFutureSub -> MaterialTheme.colorScheme.primary
                            subscription.isTrial -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Konfiguracja tekstów płatności/startu
                    val paymentText = when {
                        isFutureSub -> {
                            when {
                                daysLeft == 0 -> stringResource(id = R.string.future_starts_today)
                                daysLeft == 1 -> stringResource(id = R.string.future_starts_tomorrow)
                                daysLeft < 0 -> "Rozpoczęto"
                                else -> stringResource(id = R.string.future_starts_in_days, daysLeft)
                            }
                        }
                        subscription.isTrial -> {
                            when {
                                daysLeft == 0 -> stringResource(id = R.string.trial_ends_today)
                                daysLeft == 1 -> stringResource(id = R.string.trial_ends_tomorrow)
                                daysLeft < 0 -> "Zakończono okres próbny"
                                else -> stringResource(id = R.string.trial_ends_in_days, daysLeft)
                            }
                        }
                        daysLeft <= 3 -> {
                            when {
                                daysLeft == 0 -> stringResource(id = R.string.payment_today)
                                daysLeft == 1 -> stringResource(id = R.string.payment_tomorrow)
                                daysLeft < 0 -> "Termin minął"
                                else -> stringResource(id = R.string.payment_in_days, daysLeft)
                            }
                        }
                        else -> {
                            stringResource(id = R.string.payment_date_format, formattedTargetDate)
                        }
                    }

                    val paymentColor = when {
                        isFutureSub -> MaterialTheme.colorScheme.primary
                        subscription.isTrial -> MaterialTheme.colorScheme.tertiary
                        daysLeft <= 3 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = paymentText,
                        style = MaterialTheme.typography.labelSmall,
                        color = paymentColor,
                        fontWeight = if (daysLeft <= 3 || subscription.isTrial || isFutureSub) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}