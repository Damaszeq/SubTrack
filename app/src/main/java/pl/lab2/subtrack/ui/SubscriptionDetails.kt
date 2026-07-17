package pl.lab2.subtrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import pl.lab2.subtrack.R
import pl.lab2.subtrack.data.resolvePlanName
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import androidx.compose.ui.platform.LocalLocale
import pl.lab2.subtrack.data.local.entities.PaymentHistoryEntity
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subId: String?,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    // ==========================================
    // 1. INICJALIZACJA, SKOPY I FORMATOWANIE
    // ==========================================
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }

    // ==========================================
    // 2. STAN DATA-FLOW (POBIERANIE DANYCH)
    // ==========================================
    val subscription = remember(subId, viewModel.subscriptions.collectAsState().value, viewModel.archivedSubscriptions.collectAsState().value) {
        viewModel.getSubscriptionById(subId ?: "")
    }
    val isFutureSub = subscription != null && subscription.startDate > System.currentTimeMillis()
    val isArchived = subscription?.status == SubscriptionStatus.ARCHIVED

    val realPayments by if (subscription != null) {
        viewModel.getPaymentsForSubscription(subscription.id ?: 0L).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PaymentHistoryEntity>()) }
    }

    // ==========================================
    // 3. STANY INTERFEJSU (DIALOGI I GESTY)
    // ==========================================
    var showDeleteDialog by remember { mutableStateOf(false) }
    var paymentToEdit by remember { mutableStateOf<PaymentHistoryEntity?>(null) }
    var paymentToDelete by remember { mutableStateOf<PaymentHistoryEntity?>(null) }
    var editAmountText by remember { mutableStateOf("") }
    var activeDismissState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    // ==========================================
    // 4. LOGIKA WYCZENIA DATY KOLEJNEJ PŁATNOŚCI
    // ==========================================
    val nextPaymentFormatted = remember(subscription?.nextPaymentDate, subscription?.billingCycle) {
        if (subscription == null || isArchived) "" else {
            val now = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = subscription.nextPaymentDate
                val cycle = subscription.billingCycle.lowercase()

                while (timeInMillis < now) {
                    when {
                        cycle.contains("tydzień") || cycle.contains("week") || cycle.contains("周") -> {
                            add(java.util.Calendar.WEEK_OF_YEAR, 1)
                        }
                        cycle.contains("kwartał") || cycle.contains("quarter") || cycle.contains("季度") -> {
                            add(java.util.Calendar.MONTH, 3)
                        }
                        cycle.contains("rok") || cycle.contains("year") || cycle.contains("年") -> {
                            add(java.util.Calendar.YEAR, 1)
                        }
                        else -> {
                            add(java.util.Calendar.MONTH, 1)
                        }
                    }
                }
            }
            dateFormatter.format(Date(calendar.timeInMillis))
        }
    }

    // ==========================================
    // 5. MODALNE DIALOGI (ALERT DIALOGS)
    // ==========================================

    // Dialog edycji kwoty transakcji
    if (paymentToEdit != null) {
        AlertDialog(
            onDismissRequest = { paymentToEdit = null },
            title = { Text(stringResource(id = R.string.dialog_edit_payment_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(id = R.string.dialog_edit_payment_desc, dateFormatter.format(Date(paymentToEdit!!.paymentDate))),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = editAmountText,
                        onValueChange = { editAmountText = it },
                        label = { Text(stringResource(id = R.string.dialog_edit_payment_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newAmount = editAmountText.toDoubleOrNull()
                        if (newAmount != null && paymentToEdit != null) {
                            viewModel.updateTransactionAmount(paymentToEdit!!, newAmount)
                        }
                        paymentToEdit = null
                    },
                    enabled = editAmountText.isNotBlank() && editAmountText.toDoubleOrNull() != null
                ) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentToEdit = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Dialog potwierdzenia usunięcia pojedynczej płatności (wywoływany gestem Swipe)
    if (paymentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                scope.launch { activeDismissState?.reset() }
                paymentToDelete = null
            },
            title = { Text(stringResource(id = R.string.dialog_delete_payment_title)) },
            text = {
                Text(stringResource(id = R.string.dialog_delete_payment_desc, dateFormatter.format(Date(paymentToDelete!!.paymentDate))))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        paymentToDelete?.let { viewModel.removeTransaction(it) }
                        paymentToDelete = null
                        activeDismissState = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch { activeDismissState?.reset() }
                        paymentToDelete = null
                        activeDismissState = null
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Dialog permanentnego usunięcia całej subskrypcji
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.dialog_delete_sub_title)) },
            text = { Text(stringResource(id = R.string.dialog_delete_sub_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onBackClick()
                        subscription?.id?.let { subId ->
                            viewModel.deleteSubscription(subId.toString())
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.dialog_delete_sub_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // ==========================================
    // 6. GŁÓWNA STRUKTURA EKRANU (SCAFFOLD)
    // ==========================================
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(subscription?.name ?: stringResource(id = R.string.details), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    if (!isArchived) {
                        IconButton(onClick = { subscription?.id?.let { onEditClick(it.toString()) } }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete_desc))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            if (subscription != null) {
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isArchived) {
                        Button(
                            onClick = {
                                subscription.id?.let { subId ->
                                    viewModel.archiveSubscription(subId.toString())
                                    onBackClick()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(id = R.string.btn_archive), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Button(
                            onClick = {
                                subscription.id?.let { subId ->
                                    viewModel.unarchiveSubscription(subId.toString())
                                    onBackClick()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Unarchive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(id = R.string.btn_unarchive), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (subscription == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.subscription_not_found))
            }
            return@Scaffold
        }

        val displayedPlanName = resolvePlanName(subscription.plan, context)

        // ==========================================
        // 7. PRZEWIJANA LISTA ELEMENTÓW (LAZYCOLUMN)
        // ==========================================
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- KARTA GŁÓWNA ZE SZCZEGÓŁAMI ---
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Sekcja tagów w prawym górnym rogu
                        if (subscription.tags.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                subscription.tags.forEach { tag ->
                                    Text(
                                        text = tag.uppercase(LocalLocale.current.platformLocale),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }

                        // Informacje główne (Ikona, Nazwa, Plan)
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SubscriptionIcon(
                                serviceName = subscription.name,
                                planKey = subscription.plan,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = subscription.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = displayedPlanName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                            // Parametry subskrypcji w układzie siatki (Siatka 2x2)
                            val isNotifEnabled = !subscription.notificationSetting.equals("Wyłączone", ignoreCase = true)
                            val notificationsText = if (isNotifEnabled) stringResource(id = R.string.on) else stringResource(id = R.string.off)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    InfoColumn(
                                        label = if (subscription.isTrial) stringResource(id = R.string.label_price_trial) else stringResource(id = R.string.label_price),
                                        value = stringResource(id = R.string.price_format, subscription.price),
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoColumn(
                                        label = stringResource(id = R.string.label_cycle),
                                        value = subscription.billingCycle,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    InfoColumn(
                                        label = stringResource(id = R.string.label_start_date),
                                        value = dateFormatter.format(Date(subscription.startDate)),
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoColumn(
                                        label = stringResource(id = R.string.subscription_notifications_label),
                                        value = if (isArchived) stringResource(id = R.string.off) else notificationsText,
                                        modifier = Modifier.weight(1f),
                                        alpha = if (!isNotifEnabled || isArchived) 0.4f else 1f
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                            // Dolna sekcja statusowa (Terminy płatności / Zakończenie)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isArchived && subscription.endDate != null) {
                                    Text(text = stringResource(id = R.string.sub_status_ended), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                                    Text(text = dateFormatter.format(Date(subscription.endDate)), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = if (subscription.isTrial) stringResource(id = R.string.label_trial_end) else stringResource(id = R.string.label_next_payment), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = nextPaymentFormatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = if (subscription.isTrial) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                                        }

                                        if (subscription.isTrial) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stringResource(id = R.string.label_next_regular_price, stringResource(id = R.string.price_format, subscription.regularPrice), subscription.billingCycle.lowercase()),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

// --- BANER DLA SUBSKRYPCJI Z PRZYSZŁĄ DATĄ ROZPOCZĘCIA ---
            item {
                if (!isArchived && isFutureSub) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule, // Ikona zegarka/planowania
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column {
                                Text(
                                    text = stringResource(id = R.string.future_banner_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = stringResource(id = R.string.future_banner_desc, dateFormatter.format(Date(subscription.startDate))),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // --- BANER INFORMACYJNY DLA TRYBU TRIAL ---
            item {
                // Pokazujemy baner Trial tylko, jeśli subskrypcja już się rozpoczęła
                if (!isArchived && !isFutureSub) {
                    AnimatedVisibility(visible = subscription.isTrial) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(end = 12.dp))
                                Column {
                                    Text(text = stringResource(id = R.string.trial_banner_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    Text(text = if (subscription.trialOption.isNotEmpty()) subscription.trialOption else stringResource(id = R.string.trial_banner_desc_default), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = stringResource(id = R.string.trial_banner_future_price, stringResource(id = R.string.price_format, subscription.regularPrice)), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // --- NAGŁÓWEK SEKCJI HISTORII ---
            item {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(id = R.string.payment_history), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // --- INTERAKTYWNA LISTA HISTORII PŁATNOŚCI (SWIPE-TO-DISMISS) ---
            items(realPayments, key = { it.id ?: 0L }) { payment ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                            paymentToDelete = payment
                            false
                        } else {
                            false
                        }
                    }
                )

                LaunchedEffect(dismissState.targetValue) {
                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                        activeDismissState = dismissState
                    }
                }

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                            MaterialTheme.colorScheme.errorContainer
                        } else Color.Transparent

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 2.dp)
                                .height(72.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = color,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(modifier = Modifier.padding(end = 16.dp), contentAlignment = Alignment.CenterEnd) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.swipe_delete_desc),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    },
                    content = {
                        PaymentHistoryItem(
                            payment = payment,
                            dateFormatter = dateFormatter,
                            onClick = {
                                paymentToEdit = payment
                                editAmountText = payment.amountPaid.toString()
                            }
                        )
                    },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ==========================================
// 8. POMOCNICZE KOMPONENTY REUZYWALNE
// ==========================================

@Composable
fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
    }
}

@Composable
fun PaymentHistoryItem(
    payment: PaymentHistoryEntity,
    dateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = dateFormatter.format(Date(payment.paymentDate)), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = stringResource(id = R.string.status_paid), style = MaterialTheme.typography.bodySmall, color = Color(0xFF2C7B1E))
            }

            Text(
                text = stringResource(id = R.string.minus_price_format, payment.amountPaid),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}