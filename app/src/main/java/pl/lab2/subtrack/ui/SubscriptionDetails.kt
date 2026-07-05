package pl.lab2.subtrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import pl.lab2.subtrack.R
import pl.lab2.subtrack.data.resolvePlanName
import pl.lab2.subtrack.ui.components.SubscriptionIcon
import androidx.compose.ui.platform.LocalLocale
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus

data class PaymentHistoryMock(
    val date: String,
    val price: Double,
    val isPaid: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    subId: String?,
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current

    val subscription = remember(subId, viewModel.subscriptions.collectAsState().value, viewModel.archivedSubscriptions.collectAsState().value) {
        viewModel.getSubscriptionById(subId ?: "")
    }

    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale("pl", "PL")) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isArchived = subscription?.status == SubscriptionStatus.ARCHIVED

    // DYNAMICZNE OBLICZANIE KOLEJNEJ PŁATNOŚCI
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
                        else -> { // Domyślnie co miesiąc
                            add(java.util.Calendar.MONTH, 1)
                        }
                    }
                }
            }
            dateFormatter.format(Date(calendar.timeInMillis))
        }
    }

    // Rzeczywiste płatności wyliczane na podstawie daty i ceny z obiektu Subscription
    val payments = remember(subscription) {
        if (subscription != null) {
            val history = mutableListOf<PaymentHistoryMock>()
            val calendar = java.util.Calendar.getInstance()

            calendar.timeInMillis = subscription.startDate
            val limitDate = if (isArchived && subscription.endDate != null) subscription.endDate else System.currentTimeMillis()

            while (calendar.timeInMillis <= limitDate) {
                history.add(0, PaymentHistoryMock(
                    date = dateFormatter.format(calendar.time),
                    price = subscription.price
                ))

                when (subscription.billingCycle.lowercase()) {
                    "tydzień", "week" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    "kwartał", "quarter" -> calendar.add(java.util.Calendar.MONTH, 3)
                    "rok", "year" -> calendar.add(java.util.Calendar.YEAR, 1)
                    else -> calendar.add(java.util.Calendar.MONTH, 1)
                }
            }
            history
        } else emptyList()
    }

    // POTWIERDZENIE TRWAŁEGO USUNIĘCIA
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Trwałe usunięcie") },
            text = { Text("Czy na pewno chcesz permanentnie usunąć tę subskrypcję? Spowoduje to całkowite wyczyszczenie jej z historii i wykresów finansowych.") },
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
                    Text("Usuń trwale")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

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
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_desc)
                        )
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
            if (subscription != null && !isArchived) {
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zakończ i zarchiwizuj",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
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

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // GŁÓWNA KARTA SUBSKRYPCJI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
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

                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SubscriptionIcon(
                                serviceName = subscription.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = subscription.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = displayedPlanName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                            // POPRAWKA: Prawidłowe sprawdzenie stanu tekstowego bazy danych.
                            // Ignorujemy wielkość liter i sprawdzamy czy wartość nie jest równa "Wyłączone".
                            val isNotifEnabled = !subscription.notificationSetting.equals("Wyłączone", ignoreCase = true)
                            val notificationsText = if (isNotifEnabled) stringResource(id = R.string.on) else stringResource(id = R.string.off)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoColumn(
                                        label = stringResource(id = R.string.label_price),
                                        value = stringResource(id = R.string.price_format, subscription.price),
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoColumn(
                                        label = stringResource(id = R.string.label_cycle),
                                        value = subscription.billingCycle,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoColumn(
                                        label = "Data rozpoczęcia",
                                        value = dateFormatter.format(Date(subscription.startDate)),
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoColumn(
                                        label = stringResource(id = R.string.subscription_notifications_label),
                                        value = if (isArchived) "Wyłączone" else notificationsText,
                                        modifier = Modifier.weight(1f),
                                        alpha = if (!isNotifEnabled || isArchived) 0.4f else 1f
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isArchived && subscription.endDate != null) {
                                    Text(
                                        text = "Subskrypcja zakończona: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = dateFormatter.format(Date(subscription.endDate)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Text(
                                        text = "Kolejna płatność: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = nextPaymentFormatted,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEKCJA OKRESU PRÓBNEGO
            if (!isArchived) {
                item {
                    AnimatedVisibility(visible = subscription.isTrial) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = "Aktywny okres próbny (Trial)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = subscription.trialOption,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEKCJA HISTORII PŁATNOŚCI
            item {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.payment_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            items(payments) { payment ->
                PaymentHistoryItem(payment = payment)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
        )
    }
}

@Composable
fun PaymentHistoryItem(payment: PaymentHistoryMock) {
    OutlinedCard(
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
                Text(
                    text = payment.date,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (payment.isPaid) stringResource(id = R.string.status_paid) else stringResource(id = R.string.status_unpaid),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2C7B1E)
                )
            }

            Text(
                text = stringResource(id = R.string.minus_price_format, payment.price),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}