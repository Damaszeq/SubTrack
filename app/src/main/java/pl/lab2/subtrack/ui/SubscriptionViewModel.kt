package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lab2.subtrack.Subscription
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage(val code: String) {
    POLISH("pl"),
    ENGLISH("en")
}

class SubscriptionViewModel : ViewModel() {

    private val _subscriptions = MutableStateFlow<List<Subscription>>(
        listOf(
            Subscription(
                name = "Microsoft 365",
                plan = "Family",
                price = 42.99,
                billingCycle = "Miesiąc",
                tags = listOf("Produktywność", "Biuro", "Chmura"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "xxx",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "PlayStation Plus",
                plan = "Extra",
                price = 52.00,
                billingCycle = "Miesiąc",
                tags = listOf("Rozrywka", "Gaming", "Konsola"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Xbox Game Pass",
                plan = "Ultimate",
                price = 62.99,
                billingCycle = "Miesiąc",
                tags = listOf("Rozrywka", "Gaming", "PC", "Konsola"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "GeForce NOW",
                plan = "Priority",
                price = 49.00,
                billingCycle = "Miesiąc",
                tags = listOf("Rozrywka", "Gaming", "Chmura"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Audible",
                plan = "Premium Plus",
                price = 39.99,
                billingCycle = "Miesiąc",
                tags = listOf("Rozrywka", "Audiobooki", "Książki"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Storytel",
                plan = "Premium",
                price = 44.90,
                billingCycle = "Miesiąc",
                tags = listOf("Rozrywka", "Audiobooki", "Książki"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Adobe Creative Cloud",
                plan = "Fotografia (20GB)",
                price = 53.00,
                billingCycle = "Miesiąc",
                tags = listOf("Narzędzia", "Grafika", "Foto"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "GitHub Copilot",
                plan = "Individual",
                price = 40.00,
                billingCycle = "Miesiąc",
                tags = listOf("Produktywność", "Narzędzia", "Programowanie"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Allegro Smart!",
                plan = "Roczny",
                price = 59.90,
                billingCycle = "Rok",
                tags = listOf("Zakupy", "Dostawa"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            ),
            Subscription(
                name = "Pyszne.pl Premium",
                plan = "Smart Foodies",
                price = 14.99,
                billingCycle = "Miesiąc",
                tags = listOf("Jedzenie", "Dostawa"),
                startDate = System.currentTimeMillis(),
                isTrial = false,
                trialOption = "",
                notificationSetting = "Brak",
            )
        )
    )
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

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
        val newSub = Subscription(
            name = name,
            plan = plan,
            price = parsedPrice,
            billingCycle = billingCycle,
            tags = tags,
            startDate = startDate,
            isTrial = isTrial,
            trialOption = trialOption,
            notificationSetting = notificationSetting
        )
        _subscriptions.value += newSub
    }

    // USUWANIE SUBSKRYPCJI
    fun deleteSubscription(id: String) {
        _subscriptions.value = _subscriptions.value.filterNot { it.id == id }
    }

    fun getSubscriptionById(id: String): Subscription? {
        return _subscriptions.value.find { it.id == id }
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

        _subscriptions.value = _subscriptions.value.map { currentSub ->
            if (currentSub.id == id) {
                currentSub.copy(
                    name = name,
                    plan = plan,
                    price = parsedPrice,
                    billingCycle = billingCycle,
                    tags = tags,
                    startDate = startDate,
                    isTrial = isTrial,
                    trialOption = trialOption,
                    notificationSetting = notificationSetting
                )
            } else {
                currentSub
            }
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