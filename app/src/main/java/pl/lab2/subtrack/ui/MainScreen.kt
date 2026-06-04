package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.Subscription
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import java.util.Locale

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
                            contentDescription = "Powiadomienia"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ustawienia"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_sum_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f PLN", viewModel.getTotalSum()),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
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
    // Tymczasowe parametry do testów UI, docelowo będą wyliczane z bazy danych
    nextPaymentDate: String = "26.06.2026",
    daysLeft: Int = 4,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubscriptionIcon(
                serviceName = subscription.name,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Środek: Nazwa i Plan
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subscription.plan,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = String.format(Locale.US, "%.2f PLN", subscription.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                val paymentText = if (daysLeft <= 3) {
                    when (daysLeft) {
                        0 -> "Płatność dzisiaj!"
                        1 -> "Płatność jutro"
                        else -> "Płatność za $daysLeft dni"
                    }
                } else {
                    "Płatność: $nextPaymentDate"
                }

                val paymentColor = if (daysLeft <= 3) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Text(
                    text = paymentText,
                    style = MaterialTheme.typography.labelSmall,
                    color = paymentColor,
                    fontWeight = if (daysLeft <= 3) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}