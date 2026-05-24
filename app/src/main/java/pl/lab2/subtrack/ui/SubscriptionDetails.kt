package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subId: String?,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val subscription = subscriptions.find { it.id == subId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(subscription?.name ?: "Szczegóły") },
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
                        Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.error)
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
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.AppShortcut,
                                contentDescription = null,
                                modifier = Modifier.padding(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = subscription.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = subscription.plan, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoColumn(label = "Cena", value = "${subscription.price} PLN")
                            InfoColumn(label = "Cykl", value = subscription.billingCycle)
                        }
                    }
                }
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