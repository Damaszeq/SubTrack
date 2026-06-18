package pl.lab2.subtrack

data class Subscription(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val plan: String,
    val price: Double,
    val billingCycle: String,
    val tags: List<String> = emptyList(),

    val startDate: Long = System.currentTimeMillis(),
    val nextPaymentDate: Long = System.currentTimeMillis(),
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

fun pl.lab2.subtrack.data.local.entities.SubscriptionWithTags.toSubscription(): Subscription {
    return Subscription(
        id = subscription.id.toString(),
        name = subscription.name,
        plan = subscription.planName,
        price = subscription.price,
        billingCycle = subscription.billingCycle,
        tags = tags.map { it.name },
        startDate = subscription.startDate,
        nextPaymentDate = subscription.nextPaymentDate,
        isTrial = subscription.isTrial,
        trialOption = subscription.trialOption,
        notificationSetting = "Brak"
    )
}

fun Subscription.toUserSubscription(): pl.lab2.subtrack.data.local.entities.UserSubscription {
    val numericId = id.toLongOrNull() ?: 0L
    return pl.lab2.subtrack.data.local.entities.UserSubscription(
        id = numericId,
        name = name,
        planName = plan,
        price = price,
        billingCycle = billingCycle,
        startDate = startDate,
        nextPaymentDate = nextPaymentDate,
        status = pl.lab2.subtrack.data.local.entities.SubscriptionStatus.ACTIVE,
        isTrial = isTrial,
        trialOption = trialOption
    )
}
