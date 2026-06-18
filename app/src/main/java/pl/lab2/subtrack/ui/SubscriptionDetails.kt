package pl.lab2.subtrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import pl.lab2.subtrack.R
import pl.lab2.subtrack.ui.components.SubscriptionIcon

data class PaymentHistoryMock(
    val date: String,
    val price: Double,
    val isPaid: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subId: String?,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val subscription = subscriptions.find { it.id == subId }
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }

    // Rzeczywiste płatności wyliczane na podstawie daty i ceny z obiektu Subscription
    val payments = remember(subscription) {
        if (subscription != null) {
            val history = mutableListOf<PaymentHistoryMock>()
            val calendar = java.util.Calendar.getInstance()

            // Zaczynamy od daty startu
            calendar.timeInMillis = subscription.startDate
            val now = System.currentTimeMillis()

            // Generujemy historyczne daty, dopóki nie dojdziemy do dzisiaj
            while (calendar.timeInMillis <= now) {
                history.add(0, PaymentHistoryMock(
                    date = dateFormatter.format(calendar.time),
                    price = subscription.price
                ))

                // Przesuwamy o jeden cykl w przód
                when (subscription.billingCycle.lowercase()) {
                    "tydzień", "week" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    "kwartał", "quarter" -> calendar.add(java.util.Calendar.MONTH, 3)
                    "rok", "year" -> calendar.add(java.util.Calendar.YEAR, 1)
                    else -> calendar.add(java.util.Calendar.MONTH, 1) // domyślnie miesiąc
                }
            }
            history
        } else emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(subscription?.name ?: stringResource(id = R.string.details), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { subscription?.id?.let { onEditClick(it) } }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
                    }
                    IconButton(onClick = {
                        onBackClick()
                        subscription?.id?.let { subId ->
                            viewModel.deleteSubscription(subId)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (subscription == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.subscription_not_found))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // GŁÓWNA KARTA SUBKRYPCJI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SubscriptionIcon(
                            serviceName = subscription.name,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = subscription.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subscription.plan,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                        // ZMIANA: Pełne zbindowanie z polami modelu Subscription
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoColumn(
                                    label = stringResource(id = R.string.label_price),
                                    value = stringResource(id = R.string.price_format, subscription.price),
                                    modifier = Modifier.weight(1f)
                                )
                                InfoColumn(
                                    label = stringResource(id = R.string.label_cycle),
                                    value = subscription.billingCycle,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoColumn(
                                    label = "Data rozpoczęcia",
                                    value = dateFormatter.format(Date(subscription.startDate)),
                                    modifier = Modifier.weight(1f)
                                )
                                InfoColumn(
                                    label = "Powiadomienia",
                                    value = subscription.notificationSetting,
                                    modifier = Modifier.weight(1f),
                                    alpha = if (subscription.notificationSetting == "Brak") 0.4f else 1f
                                )
                            }
                        }
                    }
                }
            }

            // SEKCJA OKRESU PRÓBNEGO
            item {
                AnimatedVisibility(visible = subscription.isTrial) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column {
                                Text(
                                    text = "Aktywny okres próbny (Trial)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = subscription.trialOption,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // SEKCJA HISTORII PŁATNOŚCI
            item {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.payment_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            items(payments) { payment ->
                PaymentHistoryItem(payment = payment)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
        )
    }
}

@Composable
fun PaymentHistoryItem(payment: PaymentHistoryMock) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.date,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (payment.isPaid) stringResource(id = R.string.status_paid) else stringResource(id = R.string.status_unpaid),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2C7B1E)
                )
            }

            Text(
                text = stringResource(id = R.string.minus_price_format, payment.price),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}