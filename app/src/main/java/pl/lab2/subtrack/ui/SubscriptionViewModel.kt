package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lab2.subtrack.Subscription

class SubscriptionViewModel : ViewModel() {
    private val _subscriptions = MutableStateFlow<List<Subscription>>(
        listOf(
            Subscription(name = "Netflix", plan = "Premium / 4K", price = 43.00, billingCycle = "Miesiąc"),
            Subscription(name = "Spotify", plan = "Dla Rodziny", price = 29.99, billingCycle = "Miesiąc"),
            Subscription(name = "YouTube", plan = "Premium", price = 25.99, billingCycle = "Miesiąc"),
            Subscription(name = "Disney Plus", plan = "Miesięczny", price = 37.99, billingCycle = "Miesiąc"),
            Subscription(name = "Google", plan = "Miesięczny", price = 37.99, billingCycle = "Miesiąc")

    )
    )
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    fun addSubscription(name: String, plan: String, priceText: String, billingCycle: String) {
        val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
        val newSub = Subscription(
            name = name,
            plan = plan,
            price = parsedPrice,
            billingCycle = billingCycle
        )
        _subscriptions.value += newSub
    }

    fun deleteSubscription(id: String) {
        _subscriptions.value = _subscriptions.value.filterNot { it.id == id }
    }

    fun getTotalSum(): Double {
        return _subscriptions.value.sumOf { it.price }
    }
}