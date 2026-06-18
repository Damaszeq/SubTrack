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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
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
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val presets = SubscriptionPresetsData.availablePresets

    // Formatowanie daty (polski format)
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }

    // Podstawowe stany
    var selectedService by remember { mutableStateOf<ServicePreset?>(null) }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanPreset?>(null) }
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingCycleResId by remember { mutableStateOf(R.string.cycle_month) }
    var startDateLong by remember { mutableStateOf(System.currentTimeMillis()) }
    var isTrialChecked by remember { mutableStateOf(false) }
    var selectedTrialOption by remember { mutableStateOf("Pierwszy miesiąc za 0 zł, potem standard") }
    var notificationSetting by remember { mutableStateOf("Brak") }

    // STAN WYSZUKIWANIA I FILTROWANIA PO TAGACH
    var presetSearchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<String?>(null) } // null oznacza "Wszystkie"
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    // Automatyczne wyciągnięcie unikalnych tagów z dostępnych presetów
    val allAvailableTags = remember(presets) {
        presets.flatMap { it.tags }.distinct().sorted()
    }

    // Stany UI dla Dropdownów i Dialogów
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

    // SYSTEMOWY DATE PICKER DIALOG (Material 3)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateLong)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { startDateLong = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Anuluj")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.add_subscription), fontWeight = FontWeight.Bold) },
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

            // Nagłówek sekcji wyboru serwisu
            Text(
                text = stringResource(id = R.string.choose_service_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Wiersz wyszukiwarki z lekiem filtrowania
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = presetSearchQuery,
                    onValueChange = { presetSearchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedTag != null) "Szukaj w: $selectedTag..." else "Szukaj usługi..."
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                // Menu z ikoną lejka
                Box {
                    IconButton(onClick = { isFilterMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtruj po kategoriach",
                            tint = if (selectedTag != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = isFilterMenuExpanded,
                        onDismissRequest = { isFilterMenuExpanded = false }
                    ) {
                        // Opcja resetu filtra ("Wszystkie")
                        DropdownMenuItem(
                            text = { Text("Wszystkie kategorie", fontWeight = if (selectedTag == null) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                selectedTag = null
                                isFilterMenuExpanded = false
                            },
                            leadingIcon = {
                                if (selectedTag == null) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Wybrane")
                                }
                            }
                        )

                        HorizontalDivider()

                        // Dynamiczna lista tagów
                        allAvailableTags.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag, fontWeight = if (selectedTag == tag) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedTag = tag
                                    isFilterMenuExpanded = false
                                },
                                leadingIcon = {
                                    if (selectedTag == tag) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Wybrane")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Informacja o aktywnym filtrze kategorii
            if (selectedTag != null) {
                InputChip(
                    selected = true,
                    onClick = { selectedTag = null },
                    label = { Text("Kategoria: $selectedTag") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Usuń filtr",
                            modifier = Modifier.size(16.dp).padding(2.dp)
                        )
                    }
                )
            }

            // Dynamiczne filtrowanie
            val columns = 3
            val processedPresets = remember(presets, presetSearchQuery, selectedTag) {
                var filtered = presets.filter { preset ->
                    val matchesSearch = preset.serviceName.contains(presetSearchQuery, ignoreCase = true)
                    val matchesTag = selectedTag == null || preset.tags.contains(selectedTag)
                    matchesSearch && matchesTag
                }.sortedBy { it.serviceName }

                filtered.chunked(columns)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (processedPresets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak usług spełniających kryteria",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        processedPresets.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                                                .aspectRatio(1.7f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            ),
                                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                SubscriptionIcon(serviceName = preset.serviceName, modifier = Modifier.size(34.dp))
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
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // DROPDOWN WYBORU PLANU (Full Width)
            ExposedDropdownMenuBox(
                expanded = isPlanDropdownExpanded,
                onExpandedChange = { if (selectedService != null) isPlanDropdownExpanded = !isPlanDropdownExpanded }
            ) {
                val labelText = if (selectedService == null) stringResource(id = R.string.hint_select_service_first) else stringResource(id = R.string.hint_select_plan)
                OutlinedTextField(
                    value = plan,
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
                    label = { Text(labelText) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanDropdownExpanded) },
                    shape = RoundedCornerShape(12.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // POLE CENA
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(id = R.string.label_price_with_currency)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = selectedService != null,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // POLE CYKL
                ExposedDropdownMenuBox(
                    expanded = isBillingDropdownExpanded,
                    onExpandedChange = { if (selectedService != null) isBillingDropdownExpanded = !isBillingDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = stringResource(id = billingCycleResId),
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedService != null,
                        label = { Text(stringResource(id = R.string.label_cycle)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBillingDropdownExpanded) },
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

            // Układ dwukolumnowy dla Daty oraz Miejsca na przyszłe Powiadomienia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // DATA ROZPOCZĘCIA
                OutlinedTextField(
                    value = dateFormatter.format(Date(startDateLong)),
                    onValueChange = {},
                    readOnly = true,
                    enabled = selectedService != null,
                    label = { Text("Od (rozpoczęcie)") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Wybierz datę",
                            modifier = Modifier.clickable(enabled = selectedService != null) { showDatePicker = true }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).clickable(enabled = selectedService != null) { showDatePicker = true }
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Tutaj wleci pole powiadomień o zakończeniu
                    Text(
                        text = "[ Powiadomienia wkrótce ]",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // CHECKBOX OKRESU PRÓBNEGO (TRIAL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = selectedService != null) { isTrialChecked = !isTrialChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isTrialChecked,
                    onCheckedChange = { isTrialChecked = it },
                    enabled = selectedService != null
                )
                Text(
                    text = "Ta subskrypcja ma okres próbny (Trial)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedService != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // ROZSZERZONY DROPDOWN POKAZYWANY ANIMOWANIE
            AnimatedVisibility(visible = isTrialChecked && selectedService != null) {
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

            // DOLNE PRZYCISKI
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
                Button(
                    onClick = {
                        val currentTags = selectedService?.tags ?: emptyList()
                        val finalBillingCycleText = context.getString(billingCycleResId)

                        android.util.Log.d(
                            "SubTrackDebug",
                            "KLIKNIĘTO ZAPIS -> Nazwa: ${selectedService?.serviceName ?: name}, Czy trial: $isTrialChecked, Opcja: $selectedTrialOption"
                        )

                        val rawPrice = price.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
                        val formattedPriceText = String.format(java.util.Locale.US, "%.2f", rawPrice)

                        viewModel.addSubscription(
                            name = selectedService?.serviceName ?: name,
                            plan = selectedPlan?.planName ?: plan,
                            priceText = formattedPriceText,
                            billingCycle = finalBillingCycleText,
                            tags = currentTags,
                            startDate = startDateLong,
                            isTrial = isTrialChecked,
                            trialOption = selectedTrialOption,
                            notificationSetting = notificationSetting
                        )
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedService != null && price.isNotBlank()
                ) {
                    Text(text = stringResource(id = R.string.btn_save), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}