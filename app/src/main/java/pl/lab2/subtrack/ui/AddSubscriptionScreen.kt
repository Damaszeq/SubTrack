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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddSubscriptionScreen() {
    // Stany dla UI
    val isSubAccountChecked = remember { mutableStateOf(false) }
    val paymentType = remember { mutableStateOf("cykliczna") }

    // Stany dla Dropdown Menu
    val options = listOf("Co miesiąc", "Co rok", "Co 30 dni", "Co kwartał")
    val expanded = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(options[0]) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Szczegóły subskrypcji") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Wróć */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
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
                    Text("Informacje o usłudze", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text("Nazwa usługi") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text("Kategoria") },
                        placeholder = { Text("np. Streaming Audio") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Category, null) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text("Cena całkowita") },
                        suffix = { Text("PLN") },
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
                    Text("Rodzaj płatności", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentType.value == "cykliczna", onClick = { paymentType.value = "cykliczna" })
                        Text("Cykliczna")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = paymentType.value == "jednorazowa", onClick = { paymentType.value = "jednorazowa" })
                        Text("Jednorazowa")
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
                                label = { Text("Okres rozliczeniowy") },
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
                        // Widok dla subskrypcji odnawialnych (płatności co miesiąc/rok)
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            label = { Text("Data rozpoczęcia") },
                            placeholder = { Text("RRRR-MM-DD") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.DateRange, null) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        // Widok dla płatności jednorazowych
                        OutlinedTextField(
                            value = "", onValueChange = {},
                            label = { Text("Data zakończenia") },
                            placeholder = { Text("RRRR-MM-DD") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.EventBusy, null) },
                            shape = RoundedCornerShape(12.dp),
                            supportingText = { Text("Dzień wygaśnięcia dostępu") }
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
                        Text("Posiada subkonta")
                    }

                    if (isSubAccountChecked.value) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        repeat(2) { index ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = "", onValueChange = {},
                                    label = { Text("Użytkownik ${index + 1}") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = "", onValueChange = {},
                                    label = { Text("Cena") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                        TextButton(onClick = { /* TODO: Dodaj pole */ }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Dodaj kolejne subkonto")
                        }
                    }
                }
            }

            // --- PRZYCISK ZAPISU ---
            Button(
                onClick = { /* TODO: Zapisz do bazy */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Dodaj subskrypcję", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}