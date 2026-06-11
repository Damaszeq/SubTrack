package pl.lab2.subtrack.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.data.SubscriptionPresetsData
import pl.lab2.subtrack.model.ServicePreset
import pl.lab2.subtrack.model.SubscriptionPlanPreset
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscriptionId: String,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val presets = SubscriptionPresetsData.availablePresets
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }

    // Podstawowe stany formularza
    var selectedService by remember { mutableStateOf<ServicePreset?>(null) }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanPreset?>(null) }
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingCycleResId by remember { mutableStateOf(R.string.cycle_month) }
    var currentTags by remember { mutableStateOf<List<String>>(emptyList()) }

    // NOWE STANY - w pełni spięte z ViewModel i bazą danych
    var startDateLong by remember { mutableStateOf(System.currentTimeMillis()) }
    var isTrialChecked by remember { mutableStateOf(false) }
    var selectedTrialOption by remember { mutableStateOf("Pierwszy miesiąc za 0 zł, potem standard") }
    var notificationSetting by remember { mutableStateOf("Brak") }

    // Stany UI dla widoczności dropdownów / kalendarza
    var isPlanDropdownExpanded by remember { mutableStateOf(false) }
    var isBillingDropdownExpanded by remember { mutableStateOf(false) }
    var isTrialDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val billingOptions = listOf(
        R.string.cycle_week,
        R.string.cycle_month,
        R.string.cycle_quarter,
        R.string.cycle_year
    )

    val trialOptions = listOf(
        "Pierwszy miesiąc za 0 zł, potem standard",
        "Pierwszy miesiąc za 50% ceny, potem standard",
        "Własny trial (określona liczba dni wolnych)"
    )

    // Ładowanie danych istniejącej subskrypcji
    LaunchedEffect(subscriptionId) {
        viewModel.getSubscriptionById(subscriptionId)?.let { sub ->
            name = sub.name
            plan = sub.plan
            price = sub.price.toString()
            currentTags = sub.tags

            startDateLong = sub.startDate
            isTrialChecked = sub.isTrial
            if (sub.trialOption.isNotBlank()) {
                selectedTrialOption = sub.trialOption
            }
            notificationSetting = sub.notificationSetting

            val subNameLower = sub.name.lowercase().trim()
            val foundService = presets.find { preset ->
                val presetNameLower = preset.serviceName.lowercase().trim()
                presetNameLower.contains(subNameLower) || subNameLower.contains(presetNameLower)
            }

            selectedService = foundService
            selectedPlan = foundService?.plans?.find { it.planName.equals(sub.plan, ignoreCase = true) }

            billingCycleResId = when (sub.billingCycle.lowercase()) {
                "tydzień", "week", context.getString(R.string.cycle_week).lowercase() -> R.string.cycle_week
                "rok", "year", context.getString(R.string.cycle_year).lowercase() -> R.string.cycle_year
                "kwartał", "quarter", context.getString(R.string.cycle_quarter).lowercase() -> R.string.cycle_quarter
                else -> R.string.cycle_month
            }
        }
    }

    // SYSTEMOWY DATE PICKER DIALOG
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateLong)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { startDateLong = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.edit_desc), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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

            // KARTA PODGLĄDU USŁUGI
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            SubscriptionIcon(serviceName = name, modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (plan.isNotBlank()) {
                            Text(
                                text = plan,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // DROPDOWN PLANU (Full Width)
            ExposedDropdownMenuBox(
                expanded = isPlanDropdownExpanded,
                onExpandedChange = { if (selectedService != null) isPlanDropdownExpanded = !isPlanDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = plan,
                    onValueChange = { plan = it },
                    readOnly = selectedService != null,
                    label = { Text(stringResource(id = R.string.hint_select_plan)) },
                    trailingIcon = {
                        if (selectedService != null) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanDropdownExpanded)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                if (selectedService != null) {
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
            }

            // Układ dwukolumnowy dla Ceny i Cyklu rozliczeniowego
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(id = R.string.label_price_with_currency)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                ExposedDropdownMenuBox(
                    expanded = isBillingDropdownExpanded,
                    onExpandedChange = { isBillingDropdownExpanded = !isBillingDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = stringResource(id = billingCycleResId),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.label_cycle)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBillingDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
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
            }

            // Daty rozpoczęcia oraz sekcji rezerwacji powiadomień
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // POLE DATA ROZPOCZĘCIA
                OutlinedTextField(
                    value = dateFormatter.format(Date(startDateLong)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Od (rozpoczęcie)") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Zmień datę",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).clickable { showDatePicker = true }
                )

                // Wolna przestrzeń na planowane powiadomienia
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "[ Powiadomienia wkrótce ]",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // CHECKBOX: OKRES PRÓBNY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTrialChecked = !isTrialChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isTrialChecked,
                    onCheckedChange = { isTrialChecked = it }
                )
                Text(
                    text = "Ta subskrypcja ma okres próbny (Trial)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // ROZSZERZONY PANEL LOGIKI FINANSOWEJ TRIALU
            AnimatedVisibility(visible = isTrialChecked) {
                ExposedDropdownMenuBox(
                    expanded = isTrialDropdownExpanded,
                    onExpandedChange = { isTrialDropdownExpanded = !isTrialDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTrialOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Logika zmiany ceny po okresie próbnym") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTrialDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isTrialDropdownExpanded,
                        onDismissRequest = { isTrialDropdownExpanded = false }
                    ) {
                        trialOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTrialOption = option
                                    isTrialDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // PRZYCISKI AKCJI Dolnej
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                ) {
                    Text(text = stringResource(id = R.string.btn_cancel), fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        val finalBillingCycleText = context.getString(billingCycleResId)

                        viewModel.updateSubscription(
                            id = subscriptionId,
                            name = name,
                            plan = plan,
                            priceText = price,
                            billingCycle = finalBillingCycleText,
                            tags = selectedService?.tags ?: currentTags,
                            startDate = startDateLong,
                            isTrial = isTrialChecked,
                            trialOption = selectedTrialOption,
                            notificationSetting = notificationSetting
                        )
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = name.isNotBlank() && price.isNotBlank()
                ) {
                    Text(text = stringResource(id = R.string.btn_save), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}