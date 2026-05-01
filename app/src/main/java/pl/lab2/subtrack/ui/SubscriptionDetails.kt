package pl.lab2.subtrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_desc))
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_desc),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                tint = MaterialTheme.colorScheme.primary // Zmieniłem z primaryContainer na primary, żeby był widoczny!
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.mock_service_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.mock_category),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 20.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoColumn(
                                label = stringResource(id = R.string.label_total_price),
                                value = stringResource(id = R.string.mock_price)
                            )
                            InfoColumn(
                                label = stringResource(id = R.string.label_cycle),
                                value = stringResource(id = R.string.mock_cycle)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            InfoColumn(
                                label = stringResource(id = R.string.label_next_payment),
                                value = stringResource(id = R.string.mock_next_date)
                            )
                        }

                        val maSubkonta = true
                        if (maSubkonta) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_subaccounts),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )

                                SubAccountRow(
                                    name = stringResource(id = R.string.mock_user_main),
                                    price = stringResource(id = R.string.mock_sub_price)
                                )
                                SubAccountRow(
                                    name = stringResource(id = R.string.mock_user_other),
                                    price = stringResource(id = R.string.mock_sub_price)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.label_payment_history),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            items(6) { index ->
                PaymentHistoryItem(
                    date = "2024-0${6-index}-12",
                    price = stringResource(id = R.string.mock_price)
                )
            }
        }
    }
}

//--- Szczegóły subskrypcji ---
@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

//--- Wiersz subkonta ---
@Composable
fun SubAccountRow(name: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

//--- Historia płatności - element ---
@Composable
fun PaymentHistoryItem(date: String, price: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = date, style = MaterialTheme.typography.bodyLarge)
            }
            Text(text = price, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}