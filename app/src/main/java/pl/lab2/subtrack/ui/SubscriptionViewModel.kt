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
import pl.lab2.subtrack.data.local.entities.PaymentHistory
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
    CHINESE("zh")
}

// --- KLASA ENTIY DLA WYKRESU ---

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

    // NOWY STRUMIEŃ: Pobieranie zarchiwizowanych subskrypcji dla ekranu ArchiveScreen
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
                    // Matematyczny rozdział proporcjonalny ceny na przypisane kategorie
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

    // --- STRUMIEŃ HISTORII PŁATNOŚCI DLA DETALI ---

    fun getPaymentsForSubscription(subscriptionId: Long): kotlinx.coroutines.flow.Flow<List<PaymentHistory>> {
        return paymentRepository.getPaymentsForSubscriptionStream(subscriptionId)
    }

    // --- OPERACJE CRUD NA SUBSKRYPCJACH ---

    fun addSubscription(
        name: String,
        plan: String,
        priceText: String,
        billingCycle: String,
        tags: List<String>,
        startDate: Long,
        isTrial: Boolean,
        trialOption: String,
        notificationSetting: String
    ) {
        val parsedPrice = priceText.replace(",", ".").trim().toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            val nextDate = calculateNextPaymentDate(startDate, billingCycle)

            val entity = UserSubscription(
                id = 0,
                name = name,
                planKey = plan,
                price = parsedPrice,
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = notificationSetting,
                endDate = null
            )

            val tagEntities = tags.map { Tag(name = it) }
            val insertedSubscriptionId = subscriptionRepository.insertSubscriptionWithTags(entity, tagEntities, tagDao)

            // Generowanie wstecznej historii transakcji
            val historyCalendar = Calendar.getInstance().apply {
                timeInMillis = startDate
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }

            val targetNextPaymentDate = Calendar.getInstance().apply {
                timeInMillis = nextDate
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }

            while (historyCalendar.before(targetNextPaymentDate)) {
                val paymentRecord = PaymentHistory(
                    id = 0,
                    subscriptionId = insertedSubscriptionId,
                    paymentDate = historyCalendar.timeInMillis,
                    serviceName = name.trim(),
                    planName = plan.substringBefore("|").trim().ifEmpty { "Plan niestandardowy" },
                    price = parsedPrice
                )

                paymentRepository.insertPayment(paymentRecord)

                when (billingCycle.lowercase(Locale.ROOT)) {
                    "tydzień", "week", "weekly" -> historyCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                    "kwartał", "quarter" -> historyCalendar.add(Calendar.MONTH, 3)
                    "rok", "year", "yearly", "roczny", "rocznie" -> historyCalendar.add(Calendar.YEAR, 1)
                    else -> historyCalendar.add(Calendar.MONTH, 1)
                }
            }
        }
    }

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
        notificationSetting: String
    ) {
        val parsedPrice = priceText.replace(",", ".").trim().toDoubleOrNull() ?: 0.0
        val subscriptionId = id.toLongOrNull() ?: return

        viewModelScope.launch {
            val nextDate = calculateNextPaymentDate(startDate, billingCycle)

            val updatedEntity = UserSubscription(
                id = subscriptionId,
                name = name,
                planKey = plan,
                price = parsedPrice,
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = notificationSetting,
                endDate = null
            )

            val tagEntities = tags.map { Tag(name = it) }
            subscriptionRepository.updateSubscriptionWithTags(updatedEntity, tagEntities, tagDao)
        }
    }

    // LOGICZNA ARCHIWIZACJA (Zakończenie subskrypcji)
    fun archiveSubscription(id: String) {
        val numericId = id.toLongOrNull() ?: return
        viewModelScope.launch {
            val subWithTags = subscriptionRepository.getSubscriptionWithTagsStream(numericId).first()
            subWithTags?.subscription?.let { entity ->
                // Ustawiamy datę zakończenia usługi na moment bieżącego okresu płatności (trwa do najbliższej płatności)
                val calculatedEndDate = entity.nextPaymentDate
                subscriptionRepository.archiveSubscription(numericId, calculatedEndDate)
            }
        }
    }

    // TRWAŁE USUNIĘCIE (Permanentne czyszczenie błędnych wpisów)
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
        // Przeszukujemy zarówno aktywne jak i zarchiwizowane, by zapobiec pustemu ekranowi szczegółów po archiwizacji
        return subscriptions.value.find { it.id == id.toLongOrNull() }
            ?: archivedSubscriptions.value.find { it.id == id.toLongOrNull() }
    }

    // --- CONFIG SYSTEMOWY (MOTYW, JĘZYK, NOTYFIKACJE) ---

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

    // Definicja dostępnych okresów
    enum class TimePeriod(val months: Int, val label: String) {
        LAST_3_MONTHS(3, "3 miesiące"),
        LAST_6_MONTHS(6, "6 miesięcy"),
        LAST_YEAR(12, "Rok")
    }

    // Klasa reprezentująca pojedynczy słupek/punkt na wykresie
    data class TimeChartEntry(
        val label: String, // np. "05.2026" lub "Maj"
        val amount: Double // suma wydatków w tym miesiącu
    )

    // Inside SubscriptionViewModel:
    private val _selectedPeriod = MutableStateFlow(TimePeriod.LAST_6_MONTHS)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    val timeChartData: StateFlow<List<TimeChartEntry>> = combine(
        subscriptions,                  // Strumień aktywnych subskrypcji Flow<List<UserSubscription>>
        paymentRepository.getAllPaymentsStream(), // Strumień historii Flow<List<PaymentHistory>>
        _selectedPeriod
    ) { subList, payments, period ->
        val calendar = Calendar.getInstance()
        val currentYearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        // 1. Przygotowanie struktur na dane
        val sdfKey = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val sdfLabel = SimpleDateFormat("MM/yy", Locale.getDefault())

        val resultList = mutableListOf<TimeChartEntry>()

        // Ustawiamy kalendarz na najstarszy miesiąc w wybranym okresie
        val runCal = Calendar.getInstance().apply {
            add(Calendar.MONTH, -period.months + 1)
        }

        // Grupowanie HISTORII płatności po miesiącach (dla miesięcy przeszłych)
        val historyGroupedByMonth = payments.groupBy { sdfKey.format(Date(it.paymentDate)) }

        // 2. Iterujemy przez każdy miesiąc w wybranym zakresie (np. ostatnich 6 miesięcy)
        while (!runCal.after(calendar)) {
            val monthKey = sdfKey.format(runCal.time)
            val label = sdfLabel.format(runCal.time)

            val totalForMonth = if (monthKey == currentYearMonth) {
                // --- DLA BIEŻĄCEGO MIESIĄCA: Liczymy prognozę na podstawie aktywnych subskrypcji ---
                subList.sumOf { sub ->
                    // Używamy wyliczonego ekwiwalentu miesięcznego, aby zachować spójność z wykresem kołowym
                    sub.monthlyEquivalent
                }
            } else {
                // --- DLA MINIONYCH MIESIĘCY: Wyciągamy realną historię z bazy ---
                historyGroupedByMonth[monthKey]?.sumOf { it.price } ?: 0.0
            }

            resultList.add(TimeChartEntry(label, totalForMonth))
            runCal.add(Calendar.MONTH, 1) // Przejdź do kolejnego miesiąca
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