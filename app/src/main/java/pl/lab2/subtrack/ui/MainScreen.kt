package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.lab2.subtrack.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.ui.components.SubscriptionIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = stringResource(id = R.string.settings_auth)
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
                    .padding(bottom = 80.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SubscriptionItem(serviceName = "Netflix", plan = "Premium / 4K", price = "43.00")
                SubscriptionItem(serviceName = "Spotify", plan = "Dla Rodziny", price = "29.99")
                SubscriptionItem(serviceName = "YouTube", plan = "Premium", price = "25.99")
                SubscriptionItem(serviceName = "Disney Plus", plan = "Miesięczny", price = "37.99")
                SubscriptionItem(serviceName = "Allegro", plan = "Smart! Roczny", price = "59.90")
            }

            // 2. PASEK DOLNY
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Sum
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_sum_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "196.87 ${stringResource(id = R.string.currency_suffix)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                //Add button
                FloatingActionButton(
                    onClick = { /* TODO */ },
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
    serviceName: String,
    plan: String,
    price: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubscriptionIcon(
                serviceName = serviceName,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = serviceName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = plan,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "$price ${stringResource(id = R.string.currency_suffix)}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}