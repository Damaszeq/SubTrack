package pl.lab2.subtrack.models

import java.util.UUID

enum class NotificationType {
    PAYMENT_REMINDER,
    TRIAL_EXPIRING,
    SYSTEM_ALERT
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val subscriptionId: String? = null, // Powiązanie z konkretną subskrypcją
    val subscriptionName: String? = null, // Zbuforowana nazwa dla łatwego wyświetlania
    val type: NotificationType,
    val priceTriggered: Double? = null, // Kwota, której dotyczy alert
    val daysLeft: Int? = null,          // Ile dni zostało (do wyliczania stringów)
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)