package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lab2.subtrack.data.local.entities.NotificationHistory
import pl.lab2.subtrack.data.local.repositories.NotificationHistoryRepository

class NotificationViewModel(
    private val repository: NotificationHistoryRepository
) : ViewModel() {

    // Pobieramy historię bezpośrednio z bazy danych Room jako StateFlow
    val notifications: StateFlow<List<NotificationHistory>> = repository.getAllNotifications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Moduł kropki sprawdza, czy na liście znajduje się chociaż jedno nieprzeczytane powiadomienie
    val hasUnreadNotifications: StateFlow<Boolean> = notifications
        .map { list -> list.any { !it.isRead } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Oznaczanie pojedynczego powiadomienia jako przeczytane
    fun markAsRead(notification: NotificationHistory) {
        viewModelScope.launch {
            repository.updateNotification(notification.copy(isRead = true))
        }
    }

    // Oznaczanie wszystkich nieprzeczytanych powiadomień jako przeczytane
    fun markAllAsRead() {
        viewModelScope.launch {
            notifications.value.forEach { notification ->
                if (!notification.isRead) {
                    repository.updateNotification(notification.copy(isRead = true))
                }
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            repository.deleteNotificationById(notificationId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun triggerTestNotification(androidContext: android.content.Context) {
        viewModelScope.launch {
            val testNotification = NotificationHistory(
                subscriptionId = null,
                serviceName = "Netflix (Test)",
                title = "Nadchodzi płatność za Netflix (Test)",
                message = "Przypomnienie: Za 1 dni pobierzemy z Twojego konta 43,00 zł.",
                timestamp = System.currentTimeMillis(),
                isRead = false // Nowe powiadomienie domyślnie nieprzeczytane
            )

            repository.insertNotification(testNotification)

            val legacyItem = pl.lab2.subtrack.models.NotificationItem(
                id = java.util.UUID.randomUUID().toString(),
                subscriptionName = testNotification.serviceName,
                type = pl.lab2.subtrack.models.NotificationType.PAYMENT_REMINDER,
                priceTriggered = 43.00,
                daysLeft = 1,
                timestamp = testNotification.timestamp,
                isRead = false
            )
            showSystemNotification(androidContext, legacyItem)
        }
    }
}