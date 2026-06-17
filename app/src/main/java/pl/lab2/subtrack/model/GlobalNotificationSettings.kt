package pl.lab2.subtrack.model

data class GlobalNotificationSettings(
    val isEnabled: Boolean = true,
    val subscriptionReminderHours: List<Int> = listOf(24, 72), // domyślnie 24h i 72h
    val notificationTime: String = "10:00"
)