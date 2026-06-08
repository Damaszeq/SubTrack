package pl.lab2.subtrack.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
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

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val presets = SubscriptionPresetsData.availablePresets

    var selectedService by remember { mutableStateOf<ServicePreset?>(null) }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanPreset?>(null) }

    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var billingCycleResId by remember { mutableStateOf(R.string.cycle_month) }

    var isPlanDropdownExpanded by remember { mutableStateOf(false) }
    var isBillingDropdownExpanded by remember { mutableStateOf(false) }

    val billingOptions = listOf(
        R.string.cycle_week,
        R.string.cycle_month,
        R.string.cycle_quarter,
        R.string.cycle_year
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.add_subscription), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
                text = stringResource(id = R.string.choose_service_label),
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

            // 2. WYBÓR PLANU
            ExposedDropdownMenuBox(
                expanded = isPlanDropdownExpanded,
                onExpandedChange = {
                    if (selectedService != null) {
                        isPlanDropdownExpanded = !isPlanDropdownExpanded
                    }
                }
            ) {
                val labelText = if (selectedService == null) {
                    stringResource(id = R.string.hint_select_service_first)
                } else {
                    stringResource(id = R.string.hint_select_plan)
                }

                OutlinedTextField(
                    value = plan,
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
                    label = { Text(labelText) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isPlanDropdownExpanded,
                    onDismissRequest = { isPlanDropdownExpanded = false }
                ) {
                    selectedService?.plans?.forEach { planPreset ->
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.preset_plan_format, planPreset.planName, planPreset.price)) },
                            onClick = {
                                selectedPlan = planPreset
                                plan = planPreset.planName
                                price = planPreset.price.toString()

                                // Bezpieczne przypisywanie samego ID zasobu w lambdzie onClick
                                billingCycleResId = when (planPreset.billingCycle.lowercase()) {
                                    "tydzień", "week" -> R.string.cycle_week
                                    "rok", "year" -> R.string.cycle_year
                                    "kwartał", "quarter" -> R.string.cycle_quarter
                                    else -> R.string.cycle_month
                                }
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
                label = { Text(stringResource(id = R.string.label_price_with_currency)) },
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
                    value = stringResource(id = billingCycleResId), // Pobieranie stringa z ID zasobu
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
                    label = { Text(stringResource(id = R.string.label_cycle)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBillingDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isBillingDropdownExpanded,
                    onDismissRequest = { isBillingDropdownExpanded = false }
                ) {
                    billingOptions.forEach { resId ->
                        DropdownMenuItem(
                            text = { Text(stringResource(id = resId)) },
                            onClick = {
                                billingCycleResId = resId
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
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
                val context = LocalContext.current
                Button(
                    onClick = {
                        val currentTags = selectedService?.tags ?: emptyList()

                        val finalBillingCycleText = context.getString(billingCycleResId)

                        viewModel.addSubscription(name, plan, price, finalBillingCycleText, currentTags)
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedService != null && price.isNotBlank()
                ) {
                    Text(text = stringResource(id = R.string.btn_save))
                }
            }
        }
    }
}