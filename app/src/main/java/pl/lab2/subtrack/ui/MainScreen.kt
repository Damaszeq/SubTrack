package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import pl.lab2.subtrack.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

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
            // 1. Lista subskrypcji
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SubscriptionItem()
                SubscriptionItem()
                SubscriptionItem()
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
                            text = "142.90 ${stringResource(id = R.string.currency_suffix)}",
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
fun SubscriptionItem() {
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
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) { /* Tu będzie logo */ }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.placeholder_service_name),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.placeholder_plan),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = stringResource(id = R.string.placeholder_price),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}