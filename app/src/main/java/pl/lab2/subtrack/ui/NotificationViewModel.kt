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
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun markAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        _notifications.update { list ->
            list.filterNot { it.id == notificationId }
        }
    }

    fun checkAndGenerateNotifications(context: Context, subscriptionViewModel: SubscriptionViewModel) {
        viewModelScope.launch {
            try {
                val activeSubs = subscriptionViewModel.subscriptions.value ?: emptyList()
                val currentTime = System.currentTimeMillis()
                val newNotificationsList = mutableListOf<NotificationItem>()

                for (sub in activeSubs) {
                    val isEnabled = sub.notificationSetting == "true"

                    if (isEnabled) {
                        val nextPayment = sub.nextPaymentDate
                        val price = sub.price
                        val name = sub.name

                        val diffInMs = nextPayment - currentTime
                        // Obliczamy różnicę czasu w dniach
                        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs).toInt()

                        // Logika: Generuj powiadomienie, gdy płatność nastąpi za 0, 1, 2 lub 3 dni
                        if (diffInDays in 0..3) {
                            val notification = NotificationItem(
                                id = UUID.randomUUID().toString(),
                                subscriptionName = name,
                                type = if (sub.isTrial) NotificationType.TRIAL_EXPIRING else NotificationType.PAYMENT_REMINDER,
                                priceTriggered = price,
                                daysLeft = diffInDays,
                                timestamp = System.currentTimeMillis(),
                                isRead = false
                            )
                            newNotificationsList.add(notification)

                            showSystemNotification(context, notification)
                        }
                    }
                }

                _notifications.update { currentList ->
                    val filteredCurrent = currentList.filterNot { old ->
                        newNotificationsList.any { new -> new.subscriptionName == old.subscriptionName }
                    }
                    newNotificationsList + filteredCurrent
                }

            } catch (e: Exception) {
                android.util.Log.e("SUBTRACK_NOTIF_ERROR", "Błąd podczas walidacji terminów płatności: ${e.message}", e)
            }
        }
    }

    fun triggerTestNotification(context: Context) {
        val testNotification = NotificationItem(
            id = UUID.randomUUID().toString(),
            subscriptionName = "Netflix (Test)",
            type = NotificationType.PAYMENT_REMINDER,
            priceTriggered = 43.00,
            daysLeft = 1,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )

        _notifications.update { currentList ->
            listOf(testNotification) + currentList
        }

        showSystemNotification(context, testNotification)
    }
}