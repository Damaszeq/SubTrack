package pl.lab2.subtrack.ui

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
import androidx.compose.runtime.remember // DODANY IMPORT
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import pl.lab2.subtrack.ui.components.SubscriptionIcon

data class PaymentHistoryMock(
    val date: String,
    val price: Double,
    val status: String = "Opłacono"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subId: String?,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val subscription = subscriptions.find { it.id == subId }

    val mockPayments = remember(subscription) {
        if (subscription != null) {
            listOf(
                PaymentHistoryMock(date = "24 Maj 2026", price = subscription.price),
                PaymentHistoryMock(date = "24 Kwiecień 2026", price = subscription.price),
                PaymentHistoryMock(date = "24 Marzec 2026", price = subscription.price)
            )
        } else emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(subscription?.name ?: "Szczegóły", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edycja */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                    }
                    IconButton(onClick = {
                        if (subId != null) {
                            viewModel.deleteSubscription(subId)
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Usuń",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (subscription == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nie znaleziono subskrypcji")
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoColumn(label = "Cena", value = "${subscription.price} PLN")
                            InfoColumn(label = "Cykl", value = subscription.billingCycle)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Historia płatności",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            //lista wierszy płatności
            items(mockPayments) { payment ->
                PaymentHistoryItem(payment = payment)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
                    text = payment.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2C7B1E)
                )
            }

            Text(
                text = String.format(Locale.US, "-%.2f PLN", payment.price),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}