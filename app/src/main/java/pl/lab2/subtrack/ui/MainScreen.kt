package pl.lab2.subtrack.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.Subscription
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SubscriptionViewModel,
    onAddClick: () -> Unit,
    onDetailsClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {

    val subscriptions by viewModel.subscriptions.collectAsState()
    val totalMonthlyCost by viewModel.totalMonthlyCost.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(id = R.string.notifications_title)
                        )
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
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // LISTA SUBSKRYPCJI (scrolluje się pod dolnym paskiem)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                subscriptions.forEach { sub ->
                    SubscriptionItem(
                        subscription = sub,
                        onClick = { onDetailsClick(sub.id) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {


                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    shape = RoundedCornerShape(16.dp)
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
fun SubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val calendar = remember(subscription.nextPaymentDate) {
        java.util.Calendar.getInstance().apply {
            timeInMillis = subscription.nextPaymentDate

            // Pętla korygująca: przesuwaj datę do przodu, aż będzie w przyszłości
            while (timeInMillis < now) {
                when (subscription.billingCycle.lowercase()) {
                    "tydzień", "week" -> add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    "kwartał", "quarter" -> add(java.util.Calendar.MONTH, 3)
                    "rok", "year" -> add(java.util.Calendar.YEAR, 1)
                    else -> add(java.util.Calendar.MONTH, 1)
                }
            }
        }
    }

    val nextFutureDate = calendar.timeInMillis
    val dateFormatter = remember { java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()) }
    val formattedDate = dateFormatter.format(java.util.Date(nextFutureDate))

    // Obliczamy dni do przyszłej płatności
    val daysLeft = ((nextFutureDate - now) / (1000 * 60 * 60 * 24)).toInt()

    val cardColors = if (subscription.isTrial) {
        CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.12f))
    } else {
        CardDefaults.outlinedCardColors()
    }

    val cardBorder = if (subscription.isTrial) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
    } else {
        CardDefaults.outlinedCardBorder()
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
            if (subscription.isTrial) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "OKRES PRÓBNY (TRIAL)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SubscriptionIcon(serviceName = subscription.name, modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = subscription.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = subscription.plan, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%.2f PLN", subscription.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (subscription.isTrial) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val paymentText = if (subscription.isTrial) {
                        when (daysLeft) {
                            0 -> "Koniec dzisiaj"
                            1 -> "Koniec jutro"
                            else -> "Koniec za $daysLeft dni"
                        }
                    } else if (daysLeft <= 3) {
                        when (daysLeft) {
                            0 -> stringResource(id = R.string.payment_today)
                            1 -> stringResource(id = R.string.payment_tomorrow)
                            else -> stringResource(id = R.string.payment_in_days, daysLeft)
                        }
                    } else {
                        stringResource(id = R.string.payment_date_format, formattedDate)
                    }

                    val paymentColor = if (subscription.isTrial) {
                        MaterialTheme.colorScheme.tertiary
                    } else if (daysLeft <= 3) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = paymentText,
                        style = MaterialTheme.typography.labelSmall,
                        color = paymentColor,
                        fontWeight = if (daysLeft <= 3 || subscription.isTrial) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}