package pl.lab2.subtrack

data class Subscription(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val plan: String,
    val price: Double,
    val billingCycle: String,
    val tags: List<String> = emptyList(),

    val startDate: Long = System.currentTimeMillis(),
    val isTrial: Boolean = false,
    val trialOption: String = "",
    val notificationSetting: String = "Brak"
) {
    val monthlyEquivalent: Double
        get() = when (billingCycle.lowercase()) {
            "tydzień", "week" -> price * 4.33 // średnia liczba tygodni w miesiącu
            "kwartał", "quarter" -> price / 3.0
            "rok", "year" -> price / 12.0
            else -> price // domyślnie traktujemy jako miesięczny
        }
}