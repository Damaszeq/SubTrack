package pl.lab2.subtrack.model

data class SubscriptionPlanPreset(
    val planNameRes: Int,
    val price: Double,
    val billingCycle: String = "Miesiąc"
)

data class ServicePreset(
    val serviceName: String,
    val plans: List<SubscriptionPlanPreset>,
    val tagsRes: List<Int>
)