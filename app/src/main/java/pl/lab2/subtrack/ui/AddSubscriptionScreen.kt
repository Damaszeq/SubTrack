package pl.lab2.subtrack.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

data class CustomIconPreset(
    val id: String,
    val icon: ImageVector,
    val label: String
)

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val presets = SubscriptionPresetsData.availablePresets
    val globalNotifSetting by viewModel.isNotificationsEnabledGlobal.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }

    var isCustomMode by remember { mutableStateOf(false) }

    var selectedService by remember { mutableStateOf<ServicePreset?>(null) }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanPreset?>(null) }
    var name by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // POPRAWKA: Stan przechowujący cenę regularną (po zakończeniu triala)
    var regularPrice by remember { mutableStateOf("") }

    var billingCycleResId by remember { mutableStateOf(R.string.cycle_month) }
    var startDateLong by remember { mutableStateOf(System.currentTimeMillis()) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    var isTrialChecked by remember { mutableStateOf(false) }
    var selectedTrialOption by remember { mutableStateOf("Pierwszy miesiąc za 0 zł, potem standard") }

    LaunchedEffect(globalNotifSetting) {
        isNotificationEnabled = globalNotifSetting
    }

    var presetSearchQuery by remember { mutableStateOf("") }
    var selectedTagResId by remember { mutableStateOf<Int?>(null) }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    var customSelectedIconId by remember { mutableStateOf("custom_star") }
    var customSelectedTagResId by remember { mutableStateOf<Int?>(null) }
    var isCustomTagMenuExpanded by remember { mutableStateOf(false) }

    val allAvailableTags = remember(presets) {
        presets.flatMap { it.tagsRes }.distinct().sorted()
    }

    val tagCounts = remember(presets) {
        allAvailableTags.associateWith { tagRes ->
            presets.count { it.tagsRes.contains(tagRes) }
        }
    }

    val basicIcons = remember {
        listOf(
            CustomIconPreset("custom_star", Icons.Default.Star, "Inne"),
            CustomIconPreset("custom_gym", Icons.Default.FitnessCenter, "Sport"),
            CustomIconPreset("custom_home", Icons.Default.Home, "Dom / Rachunki"),
            CustomIconPreset("custom_code", Icons.Default.Code, "Software"),
            CustomIconPreset("custom_car", Icons.Default.DirectionsCar, "Transport"),
            CustomIconPreset("custom_school", Icons.Default.School, "Edukacja"),
            CustomIconPreset("custom_medical", Icons.Default.LocalHospital, "Zdrowie"),
            CustomIconPreset("custom_shopping", Icons.Default.ShoppingCart, "Zakupy"),
            CustomIconPreset("custom_money", Icons.Default.AttachMoney, "Finanse"),
            CustomIconPreset("custom_game", Icons.Default.Gamepad, "Gry / VOD")
        )
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isCustomMode = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCustomMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Z szablonu", color = if (!isCustomMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = {
                        isCustomMode = true
                        selectedService = null
                        selectedPlan = null
                        name = ""
                        plan = ""
                        price = ""
                        regularPrice = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCustomMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Własna usługa", color = if (isCustomMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            if (!isCustomMode) {
                Text(
                    text = stringResource(id = R.string.choose_service_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeTagName = selectedTagResId?.let { stringResource(id = it) }
                    OutlinedTextField(
                        value = presetSearchQuery,
                        onValueChange = { presetSearchQuery = it },
                        placeholder = { Text(text = if (activeTagName != null) "Szukaj w: $activeTagName..." else "Szukaj usługi...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(onClick = { isFilterMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtruj po kategoriach",
                                tint = if (selectedTagResId != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(expanded = isFilterMenuExpanded, onDismissRequest = { isFilterMenuExpanded = false }) {
                            DropdownMenuItem(
                                text = {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Wszystkie", fontWeight = if (selectedTagResId == null) FontWeight.Bold else FontWeight.Normal)
                                        Text("(${presets.size})", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                                    }
                                },
                                onClick = { selectedTagResId = null; isFilterMenuExpanded = false },
                                leadingIcon = { if (selectedTagResId == null) Icon(imageVector = Icons.Default.Check, contentDescription = "Wybrane") }
                            )
                            HorizontalDivider()
                            allAvailableTags.forEach { tagRes ->
                                val count = tagCounts[tagRes] ?: 0
                                DropdownMenuItem(
                                    text = {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = stringResource(id = tagRes), fontWeight = if (selectedTagResId == tagRes) FontWeight.Bold else FontWeight.Normal)
                                            Text("($count)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                                        }
                                    },
                                    onClick = { selectedTagResId = tagRes; isFilterMenuExpanded = false },
                                    leadingIcon = { if (selectedTagResId == tagRes) Icon(imageVector = Icons.Default.Check, contentDescription = "Wybrane") }
                                )
                            }
                        }
                    }
                }

                val filteredList = remember(presets, presetSearchQuery, selectedTagResId) {
                    presets.filter { preset ->
                        val matchesSearch = preset.serviceName.contains(presetSearchQuery, ignoreCase = true)
                        val matchesTag = selectedTagResId == null || preset.tagsRes.contains(selectedTagResId)
                        matchesSearch && matchesTag
                    }.sortedByDescending { it.popularityWeight }
                }

                val processedPresets = remember(filteredList) { filteredList.chunked(3) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        processedPresets.forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                for (i in 0 until 3) {
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
                                                regularPrice = ""
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1.25f),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            ),
                                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                SubscriptionIcon(
                                                    serviceName = preset.serviceName,
                                                    modifier = Modifier.size(38.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = preset.serviceName,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    } else { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                ExposedDropdownMenuBox(
                    expanded = isPlanDropdownExpanded,
                    onExpandedChange = { if (selectedService != null) isPlanDropdownExpanded = !isPlanDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = plan,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedService != null,
                        label = { Text(if (selectedService == null) stringResource(id = R.string.hint_select_service_first) else stringResource(id = R.string.hint_select_plan)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanDropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isPlanDropdownExpanded, onDismissRequest = { isPlanDropdownExpanded = false }) {
                        selectedService?.plans?.forEach { planPreset ->
                            val localizedPlanName = stringResource(id = planPreset.planNameRes)
                            DropdownMenuItem(
                                text = { Text("$localizedPlanName (${planPreset.price} zł)") },
                                onClick = {
                                    selectedPlan = planPreset
                                    plan = localizedPlanName

                                    // POPRAWKA: Inteligentne mapowanie cen w zależności od zaznaczonego triala
                                    if (isTrialChecked) {
                                        regularPrice = planPreset.price.toString()
                                        price = if (selectedTrialOption.contains("0 zł")) "0.00" else String.format(Locale.US, "%.2f", planPreset.price * 0.5)
                                    } else {
                                        price = planPreset.price.toString()
                                        regularPrice = ""
                                    }

                                    billingCycleResId = when (planPreset.billingCycle.lowercase()) {
                                        "tydzień", "week", "weekly" -> R.string.cycle_week
                                        "rok", "year", "yearly" -> R.string.cycle_year
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
            else {
                Text(text = "Konfiguracja własnej usługi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa własna usługi") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = plan,
                    onValueChange = { plan = it },
                    label = { Text("Nazwa planu (Opcjonalnie)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = isCustomTagMenuExpanded,
                        onExpandedChange = { isCustomTagMenuExpanded = !isCustomTagMenuExpanded }
                    ) {
                        val categoryLabel = if (customSelectedTagResId != null) stringResource(id = customSelectedTagResId!!) else "Wybierz kategorię (Wymagane) *"
                        OutlinedTextField(
                            value = categoryLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategoria subskrypcji") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCustomTagMenuExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = isCustomTagMenuExpanded, onDismissRequest = { isCustomTagMenuExpanded = false }) {
                            allAvailableTags.forEach { tagRes ->
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = tagRes)) },
                                    onClick = { customSelectedTagResId = tagRes; isCustomTagMenuExpanded = false }
                                )
                            }
                        }
                    }
                }

                Text(text = "Wybierz ikonę reprezentacyjną:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    basicIcons.forEach { iconPreset ->
                        val isIconSelected = customSelectedIconId == iconPreset.id
                        FilterChip(
                            selected = isIconSelected,
                            onClick = { customSelectedIconId = iconPreset.id },
                            label = { Icon(imageVector = iconPreset.icon, contentDescription = iconPreset.label, modifier = Modifier.size(24.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // POPRAWKA: Dynamiczna etykieta głównego pola ceny zależna od stanu isTrialChecked
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(if (isTrialChecked) "Cena w trialu (PLN)" else stringResource(id = R.string.label_price_with_currency)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = isCustomMode || selectedService != null,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                ExposedDropdownMenuBox(
                    expanded = isBillingDropdownExpanded,
                    onExpandedChange = { if (isCustomMode || selectedService != null) isBillingDropdownExpanded = !isBillingDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = stringResource(id = billingCycleResId),
                        onValueChange = {},
                        readOnly = true,
                        enabled = isCustomMode || selectedService != null,
                        label = { Text("Cykl płatności") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBillingDropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isBillingDropdownExpanded, onDismissRequest = { isBillingDropdownExpanded = false }) {
                        billingOptions.forEach { optionResId ->
                            DropdownMenuItem(
                                text = { Text(stringResource(id = optionResId)) },
                                onClick = {
                                    billingCycleResId = optionResId
                                    isBillingDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // POPRAWKA: Nowe pole wprowadzania ceny regularnej, widoczne tylko przy aktywnym trialu
            AnimatedVisibility(
                visible = isTrialChecked,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                OutlinedTextField(
                    value = regularPrice,
                    onValueChange = { regularPrice = it },
                    label = { Text("Cena regularna po okresie próbnym (PLN) *") },
                    placeholder = { Text("np. 39.99") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = dateFormatter.format(Date(startDateLong)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Data rozpoczęcia płatności") },
                trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Wybierz datę") } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Okres próbny (Trial)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("Zaznacz, jeśli usługa obecnie ma status darmowego lub płatnego okresu próbnego.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isTrialChecked,
                            onCheckedChange = { checked ->
                                isTrialChecked = checked
                                // Dostosowanie cen przy przełączeniu przełącznika w locie
                                if (checked && selectedPlan != null) {
                                    regularPrice = selectedPlan!!.price.toString()
                                    price = if (selectedTrialOption.contains("0 zł")) "0.00" else String.format(Locale.US, "%.2f", selectedPlan!!.price * 0.5)
                                } else if (!checked) {
                                    if (selectedPlan != null) price = selectedPlan!!.price.toString()
                                    regularPrice = ""
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = isTrialChecked) {
                        ExposedDropdownMenuBox(
                            expanded = isTrialDropdownExpanded,
                            onExpandedChange = { isTrialDropdownExpanded = !isTrialDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedTrialOption,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Wariant okresu próbnego") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTrialDropdownExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = isTrialDropdownExpanded, onDismissRequest = { isTrialDropdownExpanded = false }) {
                                trialOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedTrialOption = option
                                            isTrialDropdownExpanded = false

                                            // Przepisanie cen po zmianie wariantu triala z listy
                                            if (selectedPlan != null) {
                                                price = if (option.contains("0 zł")) "0.00" else String.format(Locale.US, "%.2f", selectedPlan!!.price * 0.5)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Przypomnienia o płatnościach", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Wyślij powiadomienie push przed pobraniem środków z konta.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isNotificationEnabled, onCheckedChange = { isNotificationEnabled = it })
            }

            Spacer(modifier = Modifier.weight(1f))

            // POPRAWKA: Przekazywanie stałej wartości domyślnej lub wpisanej ceny regularnej do funkcji ViewModelu
            val isSaveEnabled = name.isNotBlank() && price.isNotBlank() &&
                    (!isTrialChecked || regularPrice.isNotBlank()) &&
                    (isCustomMode && customSelectedTagResId != null || !isCustomMode && selectedService != null)

            Button(
                onClick = {
                    val finalBillingCycleString = context.getString(billingCycleResId)

                    val finalTags = if (isCustomMode) {
                        customSelectedTagResId?.let { listOf(context.getString(it)) } ?: emptyList()
                    } else {
                        selectedService?.tagsRes?.map { context.getString(it) } ?: emptyList()
                    }

                    // Konwersja ceny regularnej do przekazania
                    val cleanRegularPrice = regularPrice.toDoubleOrNull() ?: price.toDoubleOrNull() ?: 0.0

                    viewModel.addSubscription(
                        name = name,
                        plan = plan.ifEmpty { "Standard" },
                        priceText = price,
                        billingCycle = finalBillingCycleString,
                        tags = finalTags,
                        startDate = startDateLong,
                        isTrial = isTrialChecked,
                        trialOption = if (isTrialChecked) selectedTrialOption else "",
                        isNotificationEnabled = isNotificationEnabled,
                        regularPrice = cleanRegularPrice // DODANE: Przekazujemy cenę po okresie próbnym
                    )
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = isSaveEnabled
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zapisz subskrypcję", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}