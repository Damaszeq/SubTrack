package pl.lab2.subtrack.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    // Stany rozwijania list Dropdown dla planu i cyklu
    var isPlanDropdownExpanded by remember { mutableStateOf(false) }
    var isBillingDropdownExpanded by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

// SECTION: NAGŁÓWEK WYBORU
            Text(
                text = "Wybierz usługę z listy:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 1. TRZYKOLUMNOWA SIATKA (GRID) Z OGRANICZONĄ WYSOKOŚCIĄ I SCROLLEM
            val columns = 3
            val chunkedPresets = remember(presets) {
                presets.sortedBy { it.serviceName }.chunked(columns)
            }
            val gridScrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(gridScrollState)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    chunkedPresets.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (i in 0 until columns) {
                                if (i < rowItems.size) {
                                    val preset = rowItems[i]
                                    val isSelected = selectedService == preset

                                    Card(
                                        onClick = {
                                            selectedService = preset
                                            name = preset.serviceName
                                            selectedPlan = null
                                            plan = ""
                                            price = ""
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.75f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            }
                                        ),
                                        border = if (isSelected) {
                                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                        } else null
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            SubscriptionIcon(
                                                serviceName = preset.serviceName,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = preset.serviceName,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // 2. WYBÓR PLANU (Odblokowany dopiero po kliknięciu usługi)
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
                    label = { Text(if (selectedService == null) "Najpierw wybierz usługę powyżej" else "Wybierz plan") },
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
                enabled = selectedService != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 4. CYKL ROZLICZENIOWY
            ExposedDropdownMenuBox(
                expanded = isBillingDropdownExpanded,
                onExpandedChange = {
                    if (selectedService != null) {
                        isBillingDropdownExpanded = !isBillingDropdownExpanded
                    }
                }
            ) {
                OutlinedTextField(
                    value = billingCycle,
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
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

            // DOLNE PRZYCISKI
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f)) {
                    Text(text = "Anuluj")
                }

                Button(
                    onClick = {
                        val currentTags = selectedService?.tags ?: emptyList()
                        viewModel.addSubscription(name, plan, price, billingCycle, currentTags)
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