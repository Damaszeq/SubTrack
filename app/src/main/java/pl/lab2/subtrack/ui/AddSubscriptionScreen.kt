package pl.lab2.subtrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.data.SubscriptionPresetsData
import pl.lab2.subtrack.model.ServicePreset
import pl.lab2.subtrack.model.SubscriptionPlanPreset
import pl.lab2.subtrack.ui.components.SubscriptionIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val presets = SubscriptionPresetsData.availablePresets

    var selectedService by remember { mutableStateOf<ServicePreset?>(null) }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanPreset?>(null) }

    // Główne stany pól formularza
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("Miesiąc") }

    // Stany rozwijania list Dropdown
    var isServiceDropdownExpanded by remember { mutableStateOf(false) }
    var isPlanDropdownExpanded by remember { mutableStateOf(false) }
    var isBillingDropdownExpanded by remember { mutableStateOf(false) }

    val billingOptions = listOf("Tydzień", "Miesiąc", "Kwartał", "Rok")

    val filteredPresets = remember(name) {
        if (name.isBlank()) {
            presets
        } else {
            presets.filter { it.serviceName.contains(name, ignoreCase = true) }
        }
    }

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

            //1.  WYBÓR USŁUGI
            ExposedDropdownMenuBox(
                expanded = isServiceDropdownExpanded,
                onExpandedChange = { isServiceDropdownExpanded = !isServiceDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue ->
                        name = newValue
                        isServiceDropdownExpanded = true
                        if (selectedService?.serviceName != newValue) {
                            selectedService = null
                            selectedPlan = null
                            plan = ""
                            price = ""
                        }
                    },
                    readOnly = false,
                    label = { Text("Wyszukaj lub wpisz usługę") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServiceDropdownExpanded) },
                    leadingIcon = if (selectedService != null) {
                        {
                            SubscriptionIcon(
                                serviceName = name,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                if (filteredPresets.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = isServiceDropdownExpanded,
                        onDismissRequest = { isServiceDropdownExpanded = false }
                    ) {
                        filteredPresets.forEach { preset ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    SubscriptionIcon(
                                        serviceName = preset.serviceName,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                text = { Text(preset.serviceName) },
                                onClick = {
                                    selectedService = preset
                                    name = preset.serviceName

                                    selectedPlan = null
                                    plan = ""
                                    price = ""

                                    isServiceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            //2. WYBÓR PLANU
            ExposedDropdownMenuBox(
                expanded = isPlanDropdownExpanded,
                onExpandedChange = {
                    if (selectedService != null) {
                        isPlanDropdownExpanded = !isPlanDropdownExpanded
                    }
                }
            ) {
                OutlinedTextField(
                    value = plan,
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
                    label = { Text(if (selectedService == null) "Najpierw wybierz poprawną usługę" else "Wybierz plan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isPlanDropdownExpanded,
                    onDismissRequest = { isPlanDropdownExpanded = false }
                ) {
                    selectedService?.plans?.forEach { planPreset ->
                        DropdownMenuItem(
                            text = { Text("${planPreset.planName} (${planPreset.price} PLN)") },
                            onClick = {
                                selectedPlan = planPreset
                                plan = planPreset.planName
                                price = planPreset.price.toString()
                                billingCycle = planPreset.billingCycle
                                isPlanDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. CENA
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Cena (PLN)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            //  4. CYKL ROZLICZENIOWY
            ExposedDropdownMenuBox(
                expanded = isBillingDropdownExpanded,
                onExpandedChange = { isBillingDropdownExpanded = !isBillingDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = billingCycle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cykl rozliczeniowy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBillingDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isBillingDropdownExpanded,
                    onDismissRequest = { isBillingDropdownExpanded = false }
                ) {
                    billingOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                billingCycle = option
                                isBillingDropdownExpanded = false
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
                    enabled = selectedService != null && price.isNotBlank()
                ) {
                    Text(text = "Zapisz")
                }
            }
        }
    }
}