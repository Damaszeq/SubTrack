package pl.lab2.subtrack.models

import java.util.UUID

enum class NotificationType {
    PAYMENT_REMINDER,
    TRIAL_EXPIRING,
    SYSTEM_ALERT
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val subscriptionId: String? = null,
    val subscriptionName: String? = null,
    val type: NotificationType,
    val priceTriggered: Double? = null,
    val daysLeft: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val price: Double = 0.0,
    val isTrial: Boolean = false
)