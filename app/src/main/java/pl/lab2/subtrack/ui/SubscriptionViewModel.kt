package pl.lab2.subtrack.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lab2.subtrack.Subscription
import pl.lab2.subtrack.data.SettingsManager
import pl.lab2.subtrack.data.local.dao.TagDao
import pl.lab2.subtrack.data.local.entities.PaymentHistoryEntity
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.entities.UserSubscription
import pl.lab2.subtrack.data.local.repositories.PaymentRepository
import pl.lab2.subtrack.data.local.repositories.SubscriptionRepository
import pl.lab2.subtrack.data.local.repositories.TagRepository
import pl.lab2.subtrack.toSubscription
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.combine

// --- ENUMERACJE GLOBALNE ---

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class StatsViewType {
    BY_SUBSCRIPTION, BY_CATEGORY
}

enum class AppLanguage(val code: String) {
    POLISH("pl"),
    ENGLISH("en"),
    CHINESE("zh"),
    SPANISH("es")
}

// --- KLASA ENTITY DLA WYKRESU ---

data class PieChartEntry(
    val name: String,
    val value: Double,
    val color: Color
)

// --- VIEWMODEL ---

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val tagRepository: TagRepository,
    private val paymentRepository: PaymentRepository,
    private val tagDao: TagDao,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // --- PALETY KOLORYSTYCZNE (PREMIUM FINTECH) ---

    private val premiumPalette = listOf(
        Color(0xFF4361EE), // Głęboki cyjan / indygo
        Color(0xFF3A0CA3), // Ciemny, szlachetny fiolet
        Color(0xFF7209B7), // Wyrazisty fiolet
        Color(0xFFF72585), // Stonowany róż
        Color(0xFF4CC9F0), // Jasny błękit lodowy
        Color(0xFF2EC4B6), // Elegancki szmaragdowy/turkus
        Color(0xFF20A4F3)  // Klasyczny niebieski
    )

    private val categoryPalette = listOf(
        Color(0xFF2EC4B6), // Elegancki szmaragdowy
        Color(0xFF7209B7), // Szlachetny fiolet
        Color(0xFFFF9F1C), // Ciepły pomarańcz
        Color(0xFF4361EE), // Indygo
        Color(0xFFF72585), // Stonowana magenta
        Color(0xFF4CC9F0)  // Lodowy błękit
    )

    // --- STANY KONTROLI WIDOKU (STATYSTYKI) ---

    var currentViewType by mutableStateOf(StatsViewType.BY_SUBSCRIPTION)
        private set

    fun toggleViewType(viewType: StatsViewType) {
        currentViewType = viewType
    }

    // --- PODSTAWOWE STRUMIENIE DANYCH (ROOM) ---

    val subscriptions: StateFlow<List<Subscription>> = subscriptionRepository.getActiveSubscriptionsWithTagsStream()
        .map { list ->
            list.map { entity ->
                val sub = entity.toSubscription()
                Log.d("DEBUG_VIEW", "Ładowanie suba: ${sub.name}, nextPayment: ${sub.nextPaymentDate}, notif: ${sub.notificationSetting}")
                sub
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val archivedSubscriptions: StateFlow<List<Subscription>> = subscriptionRepository.getArchivedSubscriptionsWithTagsStream()
        .map { list ->
            list.map { entity ->
                entity.toSubscription()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // PRZYWRACANIE SUBSKRYPCJI Z ARCHIWUM (Zabezpieczone przed duplikatami)
    fun unarchiveSubscription(id: String) {
        val numericId = id.toLongOrNull() ?: return
        viewModelScope.launch {
            val subWithTags = subscriptionRepository.getSubscriptionWithTagsStream(numericId).first()
            subWithTags?.subscription?.let { entity ->

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                val newStartDate = today.timeInMillis

                val existingPayments = paymentRepository.getPaymentsForSubscriptionStream(entity.id).first()

                val hasPaymentToday = existingPayments.any { payment ->
                    val paymentCal = Calendar.getInstance().apply { timeInMillis = payment.paymentDate }
                    paymentCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            paymentCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                }

                val newNextPaymentDate = if (!hasPaymentToday) {
                    calculateNextPaymentDate(newStartDate, entity.billingCycle)
                } else {
                    if (entity.nextPaymentDate > today.timeInMillis) entity.nextPaymentDate
                    else calculateNextPaymentDate(newStartDate, entity.billingCycle)
                }

                val unarchivedEntity = entity.copy(
                    status = SubscriptionStatus.ACTIVE,
                    startDate = newStartDate,
                    nextPaymentDate = newNextPaymentDate,
                    endDate = null
                )
                subscriptionRepository.updateSubscription(unarchivedEntity)

                if (!hasPaymentToday) {
                    val currentPaymentRecord = PaymentHistoryEntity(
                        id = 0,
                        subscriptionId = entity.id,
                        paymentDate = newStartDate,
                        amountPaid = entity.price
                    )
                    paymentRepository.insertPayment(currentPaymentRecord)
                    Log.d("SUBTRACK_UNARCHIVE", "Nowy okres: Dodano płatność do historii.")
                } else {
                    Log.d("SUBTRACK_UNARCHIVE", "Miganie statusem: Pominięto historię i zablokowano przesunięcie daty.")
                }
            }
        }
    }

    // --- STRUMIENIE STATYSTYK (FINANCIAL STATS) ---

    val totalMonthlyCost: StateFlow<Double> = subscriptions
        .map { list ->
            list
                .filter { it.status == SubscriptionStatus.ACTIVE }
                .sumOf { it.monthlyEquivalent }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val pieChartData: StateFlow<List<PieChartEntry>> = subscriptions
        .map { subList ->
            subList.mapIndexed { index, sub ->
                PieChartEntry(
                    name = sub.name,
                    value = sub.monthlyEquivalent,
                    color = premiumPalette[index % premiumPalette.size]
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categoryChartData: StateFlow<List<PieChartEntry>> = subscriptions
        .map { subList ->
            val categoryMap = mutableMapOf<String, Double>()

            subList.forEach { sub ->
                val subTags = sub.tags
                if (subTags.isEmpty()) {
                    categoryMap["Inne"] = categoryMap.getOrDefault("Inne", 0.0) + sub.monthlyEquivalent
                } else {
                    val proportionalPrice = sub.monthlyEquivalent / subTags.size
                    subTags.forEach { tag ->
                        categoryMap[tag] = categoryMap.getOrDefault(tag, 0.0) + proportionalPrice
                    }
                }
            }

            categoryMap.entries.mapIndexed { index, entry ->
                PieChartEntry(
                    name = entry.key,
                    value = entry.value,
                    color = categoryPalette[index % categoryPalette.size]
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getPaymentsForSubscription(subscriptionId: Long): kotlinx.coroutines.flow.Flow<List<PaymentHistoryEntity>> {
        return paymentRepository.getPaymentsForSubscriptionStream(subscriptionId)
    }

    // --- OPERACJE CRUD NA SUBSKRYPCJACH ---

    // POPRAWKA: Dodano obsługę parametru regularPrice
    fun addSubscription(
        name: String,
        plan: String,
        priceText: String,
        billingCycle: String,
        tags: List<String>,
        startDate: Long,
        isTrial: Boolean,
        trialOption: String,
        isNotificationEnabled: Boolean,
        regularPrice: Double = 0.0
    ) {
        val parsedPrice = priceText.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
        val finalNotificationSetting = if (isNotificationEnabled) "1 dzień przed" else "Wyłączone"

        viewModelScope.launch {
            val nextDate = calculateNextPaymentDate(startDate, billingCycle)

            val entity = UserSubscription(
                id = 0,
                name = name,
                planKey = plan,
                price = parsedPrice,
                regularPrice = regularPrice, // Przypisanie ceny regularnej po zakończeniu triala
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = finalNotificationSetting,
                endDate = null
            )

            val tagEntities = tags.map { Tag(name = it) }
            val insertedSubscriptionId = subscriptionRepository.insertSubscriptionWithTags(entity, tagEntities, tagDao)

            val historyCalendar = Calendar.getInstance().apply {
                timeInMillis = startDate
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }

            val targetNextPaymentDate = Calendar.getInstance().apply {
                timeInMillis = nextDate
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }

            var isFirstPeriod = true

            while (historyCalendar.before(targetNextPaymentDate)) {
                // Pierwsza opłata rejestruje cenę triala (parsedPrice), kolejne już cenę regularną
                val actualAmount = if (isTrial && !isFirstPeriod) regularPrice else parsedPrice

                val paymentRecord = PaymentHistoryEntity(
                    id = 0,
                    subscriptionId = insertedSubscriptionId,
                    paymentDate = historyCalendar.timeInMillis,
                    amountPaid = actualAmount
                )

                paymentRepository.insertPayment(paymentRecord)
                isFirstPeriod = false

                when (billingCycle.lowercase(Locale.ROOT)) {
                    "tydzień", "week", "weekly" -> historyCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                    "kwartał", "quarter" -> historyCalendar.add(Calendar.MONTH, 3)
                    "rok", "year", "yearly", "roczny", "rocznie" -> historyCalendar.add(Calendar.YEAR, 1)
                    else -> historyCalendar.add(Calendar.MONTH, 1)
                }
            }
        }
    }

    // POPRAWKA: Dodano obsługę parametru regularPrice przy aktualizacji subskrypcji
    fun updateSubscription(
        id: String,
        name: String,
        plan: String,
        priceText: String,
        billingCycle: String,
        tags: List<String>,
        startDate: Long,
        isTrial: Boolean,
        trialOption: String,
        isNotificationEnabled: Boolean,
        regularPrice: Double = 0.0
    ) {
        val parsedPrice = priceText.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
        val subscriptionId = id.toLongOrNull() ?: return
        val finalNotificationSetting = if (isNotificationEnabled) "1 dzień przed" else "Wyłączone"

        viewModelScope.launch {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis

            val nextDate = if (startDate >= todayStart) {
                startDate
            } else {
                calculateNextPaymentDate(startDate, billingCycle)
            }

            // --- NAPRAWA BŁĘDU: Czyszczenie historii z pomyłkowej daty ---
            if (startDate >= todayStart) {
                // Używamy nowo dodanej metody z repozytorium płatności
                paymentRepository.deletePaymentsBeforeDate(subscriptionId, startDate)
            }

            val updatedEntity = UserSubscription(
                id = subscriptionId,
                name = name,
                planKey = plan,
                price = parsedPrice,
                regularPrice = regularPrice,
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = finalNotificationSetting,
                endDate = null
            )

            val tagEntities = tags.map { Tag(name = it) }
            subscriptionRepository.updateSubscriptionWithTags(updatedEntity, tagEntities, tagDao)
        }
    }

    fun archiveSubscription(id: String) {
        val numericId = id.toLongOrNull() ?: return
        viewModelScope.launch {
            val subWithTags = subscriptionRepository.getSubscriptionWithTagsStream(numericId).first()
            subWithTags?.subscription?.let { entity ->
                val calculatedEndDate = entity.nextPaymentDate
                subscriptionRepository.archiveSubscription(numericId, calculatedEndDate)
            }
        }
    }

    fun deleteSubscription(id: String) {
        val numericId = id.toLongOrNull() ?: return
        viewModelScope.launch {
            val subWithTags = subscriptionRepository.getSubscriptionWithTagsStream(numericId).first()
            subWithTags?.subscription?.let {
                subscriptionRepository.deleteSubscription(it)
            }
        }
    }

    fun getSubscriptionById(id: String): Subscription? {
        return subscriptions.value.find { it.id == id.toLongOrNull() }
            ?: archivedSubscriptions.value.find { it.id == id.toLongOrNull() }
    }

    // --- CONFIG SYSTEMOWY ---

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    private val _language = MutableStateFlow(AppLanguage.POLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    val isNotificationsEnabledGlobal: StateFlow<Boolean> = settingsManager.isNotificationsEnabledGlobal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val globalReminderHours: StateFlow<Set<Int>> = settingsManager.globalReminderHours
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(24))

    fun setGlobalNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setGlobalNotificationsEnabled(enabled) }
    }

    fun setGlobalReminderHours(hours: Set<Int>) {
        viewModelScope.launch { settingsManager.setGlobalReminderHours(hours) }
    }

    // --- WEWNĘTRZNE FUNKCJE POMOCNICZE (UTILITIES) ---

    private fun calculateNextPaymentDate(startDate: Long, billingCycle: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }

        while (!calendar.after(today)) {
            when (billingCycle.lowercase(Locale.ROOT)) {
                "tydzień", "week", "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "kwartał", "quarter" -> calendar.add(Calendar.MONTH, 3)
                "rok", "year", "yearly", "roczny", "rocznie" -> calendar.add(Calendar.YEAR, 1)
                else -> calendar.add(Calendar.MONTH, 1)
            }
        }
        return calendar.timeInMillis
    }

    enum class TimePeriod(val months: Int, val label: String) {
        LAST_3_MONTHS(3, "3 miesiące"),
        LAST_6_MONTHS(6, "6 miesięcy"),
        LAST_YEAR(12, "Rok")
    }

    data class TimeChartEntry(
        val label: String,
        val amount: Double
    )

    private val _selectedPeriod = MutableStateFlow(TimePeriod.LAST_6_MONTHS)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    val timeChartData: StateFlow<List<TimeChartEntry>> = combine(
        subscriptions,
        paymentRepository.getAllPaymentsStream(),
        _selectedPeriod
    ) { subList, payments, period ->
        val calendar = Calendar.getInstance()
        val currentYearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        val sdfKey = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val sdfLabel = SimpleDateFormat("MM/yy", Locale.getDefault())

        val resultList = mutableListOf<TimeChartEntry>()

        val runCal = Calendar.getInstance().apply {
            add(Calendar.MONTH, -period.months + 1)
        }

        val historyGroupedByMonth = payments.groupBy { sdfKey.format(Date(it.paymentDate)) }

        while (!runCal.after(calendar)) {
            val monthKey = sdfKey.format(runCal.time)
            val label = sdfLabel.format(runCal.time)

            val totalForMonth = if (monthKey == currentYearMonth) {
                subList.sumOf { sub -> sub.monthlyEquivalent }
            } else {
                historyGroupedByMonth[monthKey]?.sumOf { it.amountPaid } ?: 0.0
            }

            resultList.add(TimeChartEntry(label, totalForMonth))
            runCal.add(Calendar.MONTH, 1)
        }

        resultList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun changeTimePeriod(period: TimePeriod) {
        _selectedPeriod.value = period
    }
}