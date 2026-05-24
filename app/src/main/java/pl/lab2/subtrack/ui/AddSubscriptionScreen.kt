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
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("Miesiąc") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val billingOptions = listOf("Tydzień", "Miesiąc", "Kwartał", "Rok")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.add_subscription)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa usługi (np. Netflix, Spotify)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = plan,
                onValueChange = { plan = it },
                label = { Text("Plan (np. Premium, Family, Student)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Cena (np. 29.99)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = billingCycle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cykl rozliczeniowy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f)) {
                    Text(text = "Anuluj")
                }

                Button(
                    onClick = {
                        viewModel.addSubscription(name, plan, price, billingCycle)
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && price.isNotBlank()
                ) {
                    Text(text = "Zapisz")
                }
            }
        }
    }
}