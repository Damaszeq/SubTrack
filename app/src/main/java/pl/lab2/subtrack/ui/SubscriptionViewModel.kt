package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.lab2.subtrack.Subscription
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.repositories.SubscriptionRepository
import pl.lab2.subtrack.data.local.repositories.TagRepository
import pl.lab2.subtrack.toSubscription
import java.util.Calendar
import java.util.Locale
import pl.lab2.subtrack.data.SettingsManager

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage(val code: String) {
    POLISH("pl"),
    ENGLISH("en"),

    CHINESE("zh")
}

private fun calculateNextPaymentDate(startDate: Long, billingCycle: String): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = startDate

    when (billingCycle.lowercase(Locale.ROOT)) {
        "tydzień", "week" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
        "kwartał", "quarter" -> calendar.add(Calendar.MONTH, 3)
        "rok", "year" -> calendar.add(Calendar.YEAR, 1)
        else -> calendar.add(Calendar.MONTH, 1) // domyślnie miesiąc
    }
    return calendar.timeInMillis
}

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val tagRepository: TagRepository,
    private val tagDao: pl.lab2.subtrack.data.local.dao.TagDao,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val subscriptions: StateFlow<List<Subscription>> = subscriptionRepository.getActiveSubscriptionsWithTagsStream()
        .map { list ->
            list.map { entity ->
                val sub = entity.toSubscription()
                android.util.Log.d("DEBUG_VIEW", "Ładowanie suba: ${sub.name}, nextPayment: ${sub.nextPaymentDate}, notif: ${sub.notificationSetting}")
                sub
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // MOTYW APLIKACJI
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    // JĘZYK APLIKACJI
    private val _language = MutableStateFlow(AppLanguage.POLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    // --- SEKCJA USTAWIEŃ GLOBALNYCH POWIADOMIEŃ ---
    // 2. Pobieramy strumień bezpośrednio z DataStore i zamieniamy go w StateFlow dla Compose UI
    val isNotificationsEnabledGlobal: StateFlow<Boolean> = settingsManager.isNotificationsEnabledGlobal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // 3. To samo robimy dla zbioru godzin/dni powiadomień
    val globalReminderHours: StateFlow<Set<Int>> = settingsManager.globalReminderHours
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = setOf(24) // domyślnie 24 godziny (1 dzień) przed
        )

    // 4. Zapis do DataStore uruchamiamy w asynchronicznym wątku (viewModelScope)
    fun setGlobalNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setGlobalNotificationsEnabled(enabled)
        }
    }

    fun setGlobalReminderHours(hours: Set<Int>) {
        viewModelScope.launch {
            settingsManager.setGlobalReminderHours(hours)
        }
    }
    // ----------------------------------------------

    // DODAWANIE NOWEJ SUBSKRYPCJI
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
            val entity = pl.lab2.subtrack.data.local.entities.UserSubscription(
                id = 0,
                name = name,
                planKey = plan,
                price = parsedPrice,
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = pl.lab2.subtrack.data.local.entities.SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = notificationSetting
            )

            val tagEntities = tags.map { Tag(name = it) }
            subscriptionRepository.insertSubscriptionWithTags(entity, tagEntities, tagDao)
        }
    }

    // USUWANIE SUBSKRYPCJI
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
    }

    // EDYCJA SUBSKRYPCJI
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

            val updatedEntity = pl.lab2.subtrack.data.local.entities.UserSubscription(
                id = subscriptionId,
                name = name,
                planKey = plan,
                price = parsedPrice,
                billingCycle = billingCycle,
                startDate = startDate,
                nextPaymentDate = nextDate,
                status = pl.lab2.subtrack.data.local.entities.SubscriptionStatus.ACTIVE,
                isTrial = isTrial,
                trialOption = trialOption,
                notificationSetting = notificationSetting
            )

            val tagEntities = tags.map { Tag(name = it) }
            subscriptionRepository.updateSubscriptionWithTags(updatedEntity, tagEntities, tagDao)
        }
    }

    val totalMonthlyCost: StateFlow<Double> = subscriptions
        .map { list ->
            list.sumOf { it.monthlyEquivalent }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
}