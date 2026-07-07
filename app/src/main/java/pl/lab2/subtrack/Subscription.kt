package pl.lab2.subtrack

import pl.lab2.subtrack.data.local.entities.SubscriptionStatus
import pl.lab2.subtrack.data.local.entities.UserSubscription

data class Subscription(
    val id: Long? = null,
    val name: String,
    val plan: String,
    val price: Double,               // Aktualna cena (np. 1.00 PLN za pierwszy miesiąc triala)
    val billingCycle: String,
    val tags: List<String> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val nextPaymentDate: Long = System.currentTimeMillis(),
    val isTrial: Boolean = false,
    val trialOption: String = "",
    val notificationSetting: String = "true",
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val endDate: Long? = null,

    // NOWE POLE: Cena, jaka wejdzie automatycznie po zakończeniu triala
    val regularPrice: Double = 0.0
) {
    // Statystyki i wykresy biorą pod uwagę AKTUALNĄ cenę (czyli promocyjną/trialową)
    val monthlyEquivalent: Double
        get() = when (billingCycle.lowercase()) {
            "tydzień", "week" -> price * 4.33
            "kwartał", "quarter" -> price / 3.0
            "rok", "year" -> price / 12.0
            else -> price
        }
}

// Mapowanie z bazy do domeny
fun pl.lab2.subtrack.data.local.entities.SubscriptionWithTags.toSubscription(): Subscription {
    return Subscription(
        id = subscription.id,
        name = subscription.name,
        plan = subscription.planKey,
        price = subscription.price, // Aktualna cena triala z bazy
        billingCycle = subscription.billingCycle,
        tags = tags.map { it.name },
        startDate = subscription.startDate,
        nextPaymentDate = subscription.nextPaymentDate,
        isTrial = subscription.isTrial,
        trialOption = subscription.trialOption,
        notificationSetting = subscription.notificationSetting,
        status = subscription.status,
        endDate = subscription.endDate,

        // Mapowanie nowego pola z encji bazodanowej
        regularPrice = subscription.regularPrice
    )
}

// Mapowanie z domeny do bazy
fun Subscription.toUserSubscription(): UserSubscription {
    return UserSubscription(
        id = id ?: 0L,
        name = name,
        planKey = plan,
        price = price, // Zapisujemy aktualną cenę do kolumny price
        billingCycle = billingCycle,
        startDate = startDate,
        nextPaymentDate = nextPaymentDate,
        isTrial = isTrial,
        trialOption = trialOption,
        notificationSetting = notificationSetting,
        status = status,
        endDate = endDate,

        // Zapisujemy cenę regularną do nowej kolumny w bazie
        regularPrice = regularPrice
    )
}