package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddSubscriptionScreen() {
    val isSubAccountChecked = remember { mutableStateOf(false) }
    val paymentType = remember { mutableStateOf("cykliczna") }

    // Opcje zaciągane z zasobów
    val options = listOf(
        stringResource(id = R.string.period_monthly),
        stringResource(id = R.string.period_yearly),
        stringResource(id = R.string.period_30days),
        stringResource(id = R.string.period_quarterly)
    )
    val expanded = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(options[0]) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.add_sub_title)) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_desc))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SEKCJA 1: INFORMACJE GŁÓWNE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(id = R.string.header_service_info),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text(stringResource(id = R.string.label_service_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text(stringResource(id = R.string.label_category)) },
                        placeholder = { Text(stringResource(id = R.string.placeholder_category)) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Category, null) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text(stringResource(id = R.string.label_total_price_field)) },
                        suffix = { Text(stringResource(id = R.string.currency_pln)) },
                        leadingIcon = { Icon(Icons.Default.Payments, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // --- SEKCJA 2: PŁATNOŚĆ I OKRES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(id = R.string.header_payment_type),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentType.value == "cykliczna", onClick = { paymentType.value = "cykliczna" })
                        Text(stringResource(id = R.string.payment_cyclic))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = paymentType.value == "jednorazowa", onClick = { paymentType.value = "jednorazowa" })
                        Text(stringResource(id = R.string.payment_one_time))
                    }

                    if (paymentType.value == "cykliczna") {
                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = !expanded.value }
                        ) {
                            OutlinedTextField(
                                value = selectedOption.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(id = R.string.label_billing_period)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                options.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption) },
                                        onClick = {
                                            selectedOption.value = selectionOption
                                            expanded.value = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SEKCJA 3: TERMINY ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (paymentType.value == "cykliczna") {
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            label = { Text(stringResource(id = R.string.label_start_date)) },
                            placeholder = { Text(stringResource(id = R.string.placeholder_date)) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.DateRange, null) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            label = { Text(stringResource(id = R.string.label_end_date)) },
                            placeholder = { Text(stringResource(id = R.string.placeholder_date)) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.EventBusy, null) },
                            shape = RoundedCornerShape(12.dp),
                            supportingText = { Text(stringResource(id = R.string.support_end_date)) }
                        )
                    }
                }
            }

            // --- SEKCJA 4: SUBKONTA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isSubAccountChecked.value,
                            onCheckedChange = { isSubAccountChecked.value = it }
                        )
                        Text(stringResource(id = R.string.checkbox_subaccounts))
                    }

                    if (isSubAccountChecked.value) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        repeat(2) { index ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = "", onValueChange = {},
                                    label = { Text("${stringResource(id = R.string.label_user)} ${index + 1}") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = "", onValueChange = {},
                                    label = { Text(stringResource(id = R.string.label_price)) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                        TextButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(id = R.string.btn_add_subaccount))
                        }
                    }
                }
            }

            // --- PRZYCISK ZAPISU ---
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_save_subscription),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}