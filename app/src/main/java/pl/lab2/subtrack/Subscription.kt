package pl.lab2.subtrack

data class Subscription(
    val id: Long? = null,
    val name: String,
    val plan: String,
    val price: Double,
    val billingCycle: String,
    val tags: List<String> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val nextPaymentDate: Long = System.currentTimeMillis(),
    val isTrial: Boolean = false,
    val trialOption: String = "",
    val notificationSetting: String = "true"
) {
    val monthlyEquivalent: Double
        get() = when (billingCycle.lowercase()) {
            "tydzień", "week" -> price * 4.33
            "kwartał", "quarter" -> price / 3.0
            "rok", "year" -> price / 12.0
            else -> price
        }
}

// Mapowanie z bazy danych (Room) do modelu domenowego (UI)
fun pl.lab2.subtrack.data.local.entities.SubscriptionWithTags.toSubscription(): Subscription {
    return Subscription(
        id = subscription.id,
        name = subscription.name,
        plan = subscription.planKey,
        price = subscription.price,
        billingCycle = subscription.billingCycle,
        tags = tags.map { it.name },
        startDate = subscription.startDate,
        nextPaymentDate = subscription.nextPaymentDate,
        isTrial = subscription.isTrial,
        trialOption = subscription.trialOption,
        notificationSetting = subscription.notificationSetting
    )
}

// Mapowanie z modelu domenowego (UI) do bazy danych (Room)
fun Subscription.toUserSubscription(): pl.lab2.subtrack.data.local.entities.UserSubscription {
    return pl.lab2.subtrack.data.local.entities.UserSubscription(
        id = id ?: 0L,
        name = name,
        planKey = plan,
        price = price,
        billingCycle = billingCycle,
        startDate = startDate,
        nextPaymentDate = nextPaymentDate,
        status = pl.lab2.subtrack.data.local.entities.SubscriptionStatus.ACTIVE,
        isTrial = isTrial,
        trialOption = trialOption,
        notificationSetting = notificationSetting
    )
}