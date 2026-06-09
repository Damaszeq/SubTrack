package pl.lab2.subtrack.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.models.NotificationType

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        loadMockNotifications()
    }

    private fun loadMockNotifications() {
        // Generujemy przykładowe dane korzystając z nowego, czystego modelu
        _notifications.value = listOf(
            NotificationItem(
                subscriptionName = "Netflix",
                type = NotificationType.PAYMENT_REMINDER,
                priceTriggered = 43.99,
                daysLeft = 1,
                timestamp = System.currentTimeMillis() // Dzisiaj
            ),
            NotificationItem(
                subscriptionName = "Spotify",
                type = NotificationType.PAYMENT_REMINDER,
                priceTriggered = 19.99,
                daysLeft = 3,
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // Wczoraj
            ),
            NotificationItem(
                subscriptionName = "YouTube",
                type = NotificationType.TRIAL_EXPIRING,
                daysLeft = 1,
                timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000 // 2 dni temu
            ),
            NotificationItem(
                type = NotificationType.SYSTEM_ALERT,
                timestamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000, // 3 dni temu
                isRead = true
            )
        )
    }

    fun markAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
        }
    }

    fun triggerTestNotification(context: Context) {
        _notifications.value.firstOrNull()?.let {
            showSystemNotification(context, it)
        }
    }
}