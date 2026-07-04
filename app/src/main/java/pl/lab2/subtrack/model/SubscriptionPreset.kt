package pl.lab2.subtrack.model

data class SubscriptionPlanPreset(
    val planKey: String,
    val planNameRes: Int,
    val price: Double,
    val billingCycle: String = "Miesiąc"
)

data class ServicePreset(
    val serviceName: String,
    val plans: List<SubscriptionPlanPreset>,
    val tagsRes: List<Int>,
    val popularityWeight: Int = 0 // Im wyższa wartość, tym wyżej na liście
)