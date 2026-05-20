package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    onBackClick: () -> Unit
) {
    // Stany dla pól formularza (przechowują to, co wpisuje użytkownik)
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Stan dla rozwijanego menu (cykl rozliczeniowy)
    var billingCycle by remember { mutableStateOf("Miesiąc") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val billingOptions = listOf("Tydzień", "Miesiąc", "Kwartał", "Rok")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.add_subscription),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wstecz"
                        )
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
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Pole: Nazwa usługi
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa usługi (np. Netflix, Spotify)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Pole: Nazwa planu
            OutlinedTextField(
                value = plan,
                onValueChange = { plan = it },
                label = { Text("Plan (np. Premium, Family, Student)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. Pole: Cena (z klawiaturą numeryczną)
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Cena (np. 29.99)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 4. Pole: Cykl rozliczeniowy (Rozwijana lista / Exposed Dropdown Menu)
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = billingCycle,
                    onValueChange = {},
                    readOnly = true, // Użytkownik nie wpisuje z klawiatury, tylko klika
                    label = { Text("Cykl rozliczeniowy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    billingOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                billingCycle = option
                                isDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Elastyczny odstęp wypychający przyciski na sam dół ekranu
            Spacer(modifier = Modifier.weight(1f))

            // 5. Sekcja przycisków akcji
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Przycisk Anuluj
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Anuluj")
                }

                // Przycisk Zapisz
                Button(
                    onClick = {
                        // TODO: W następnym kroku zrobimy: viewModel.addSubscription(...)
                        onBackClick() // Na razie tylko wraca na ekran główny
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && price.isNotBlank() // Przycisk działa tylko gdy podano nazwę i cenę
                ) {
                    Text(text = "Zapisz")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddSubscriptionScreenPreview() {
    AddSubscriptionScreen(onBackClick = {})
}