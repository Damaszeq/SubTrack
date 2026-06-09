package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lab2.subtrack.Subscription
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
            Subscription(name = "Netflix", plan = "Standard", price = 43.00, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo")),
            Subscription(name = "Spotify", plan = "Premium Duo", price = 26.99, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Muzyka")),
            Subscription(name = "Max", plan = "Miesięczny Premium", price = 29.99, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo")),
            Subscription(name = "Canal+ Online", plan = "Canal+ Seriale i Filmy", price = 29.00, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo", "Sport")),
            Subscription(name = "Disney+", plan = "Premium 4K", price = 49.99, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo")),
            Subscription(name = "YouTube Premium", plan = "Dla studentów", price = 14.99, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo", "Muzyka")),
            Subscription(name = "Amazon Prime", plan = "Subskrypcja roczna", price = 49.00, billingCycle = "Rok", tags = listOf("Zakupy", "Dostawa", "Rozrywka")),
            Subscription(name = "Apple Music", plan = "Dla rodzin", price = 44.99, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Muzyka")),
            Subscription(name = "Player", plan = "Z reklamami", price = 10.00, billingCycle = "Miesiąc", tags = listOf("Rozrywka", "Streaming", "Wideo")),
            Subscription(name = "Xbox Game Pass", plan = "Core", price = 34.99, billingCycle = "Miesiąc", tags = listOf("Gaming", "Rozrywka", "Konsola", "PC")),
            Subscription(name = "PlayStation Plus", plan = "Essential", price = 37.00, billingCycle = "Miesiąc", tags = listOf("Gaming", "Rozrywka", "Konsola")),
            Subscription(name = "Nintendo Switch Online", plan = "Family Pack", price = 142.00, billingCycle = "Rok", tags = listOf("Gaming", "Rozrywka", "Konsola")),
            Subscription(name = "Google One", plan = "2 TB + AI Premium", price = 97.99, billingCycle = "Miesiąc", tags = listOf("Produktywność", "Chmura", "Dane")),
            Subscription(name = "ChatGPT Plus", plan = "Wersja Pro", price = 85.00, billingCycle = "Miesiąc", tags = listOf("Produktywność", "Sztuczna Inteligencja", "Narzędzia")),
            Subscription(name = "iCloud+", plan = "200 GB", price = 14.99, billingCycle = "Miesiąc", tags = listOf("Produktywność", "Chmura", "Dane")),
            Subscription(name = "Adobe Creative Cloud", plan = "Wszystkie aplikacje", price = 280.00, billingCycle = "Miesiąc", tags = listOf("Produktywność", "Praca", "Grafika")),
            Subscription(name = "Duolingo Plus", plan = "Super Duolingo", price = 34.99, billingCycle = "Miesiąc", tags = listOf("Edukacja", "Języki")),
            Subscription(name = "MultiSport", plan = "Karta pracownicza", price = 120.00, billingCycle = "Miesiąc", tags = listOf("Sport", "Zdrowie", "Karnet")),
            Subscription(name = "Tinder Gold", plan = "Pakiet 1 miesiąc", price = 49.99, billingCycle = "Miesiąc", tags = listOf("Social", "Rozmowy")),
            Subscription(name = "Fitatu Premium", plan = "Miesięczny PRO", price = 24.99, billingCycle = "Miesiąc", tags = listOf("Zdrowie", "Dieta", "Sport"))
        )
    )
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    // MOTYW APLIKACJI
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    // JEZYK APLIKACJI
    private val _language = MutableStateFlow(AppLanguage.POLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    // DODAWANIE SUBSKRYPCJI
    fun addSubscription(
        name: String,
        plan: String,
        priceText: String,
        billingCycle: String,
        tags: List<String>
    ) {
        val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
        val newSub = Subscription(
            name = name,
            plan = plan,
            price = parsedPrice,
            billingCycle = billingCycle,
            tags = tags
        )
        _subscriptions.value += newSub
    }

    // USYWANIE SUBSKRYPCJI
    fun deleteSubscription(id: String) {
        _subscriptions.value = _subscriptions.value.filterNot { it.id == id }
    }

    fun getSubscriptionById(id: String): Subscription? {
        return _subscriptions.value.find { it.id == id }
    }

    fun updateSubscription(
        id: String,
        name: String,
        plan: String,
        priceText: String,
        billingCycle: String,
        tags: List<String>
    ) {
        val parsedPrice = priceText.replace(",", ".").toDoubleOrNull() ?: 0.0

        // Mapujemy starą listę na nową, podmieniając tylko edytowany element
        _subscriptions.value = _subscriptions.value.map { currentSub ->
            if (currentSub.id == id) {
                currentSub.copy(
                    name = name,
                    plan = plan,
                    price = parsedPrice,
                    billingCycle = billingCycle,
                    tags = tags
                )
            } else {
                currentSub // resztę zostawiamy bez zmian
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