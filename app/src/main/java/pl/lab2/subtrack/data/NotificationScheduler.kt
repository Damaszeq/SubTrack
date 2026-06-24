package pl.lab2.subtrack.notification

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import pl.lab2.subtrack.data.NotificationWorker
import pl.lab2.subtrack.data.SettingsManager
import pl.lab2.subtrack.data.local.repositories.SubscriptionRepository
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.models.NotificationType
import pl.lab2.subtrack.toSubscription
import pl.lab2.subtrack.ui.showSystemNotification
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {


    private val settingsManager = SettingsManager(context)

    suspend fun checkSubscriptionsAndNotify(repository: SubscriptionRepository) {
        try {
            // 1. Sprawdzamy globalny wyłącznik powiadomień
            val isGlobalEnabled = settingsManager.isNotificationsEnabledGlobal.first()
            if (!isGlobalEnabled) {
                android.util.Log.d("SUBTRACK_SCHEDULER", "Powiadomienia globalne są wyłączone w ustawieniach.")
                return // Przerywamy, nie wysyłamy żadnych pushy
            }

            // 2. Pobieramy preferowane terminy przypomnień (np. setOf(24), czyli 24h przed)
            val preferredHours = settingsManager.globalReminderHours.first()

            val entitiesWithTags = repository.getActiveSubscriptionsWithTagsStream().first()
            val activeSubs = entitiesWithTags.map { it.toSubscription() }
            val currentTime = System.currentTimeMillis()

            for (sub in activeSubs) {
                // Sprawdzamy, czy powiadomienia dla tej konkretnej subskrypcji są włączone
                if (sub.notificationSetting == "true") {
                    val diffInMs = sub.nextPaymentDate - currentTime
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs).toInt()

                    // Przeliczamy dni na godziny (np. 1 dzień przed = 24h), aby pasowało do ustawień globalnych
                    val diffInHours = diffInDays * 24

                    // 3. DYNAMICZNY WARUNEK: Sprawdzamy, czy czas do płatności pokrywa się z wyborem użytkownika
                    val isTimeMatched = preferredHours.any { hour ->
                        diffInHours == hour || (hour == 24 && diffInDays == 1) // bezpiecznik dla zaokrągleń czasu
                    }

                    if (isTimeMatched) {
                        val notification = NotificationItem(
                            id = UUID.randomUUID().toString(),
                            subscriptionName = sub.name,
                            type = if (sub.isTrial) NotificationType.TRIAL_EXPIRING else NotificationType.PAYMENT_REMINDER,
                            priceTriggered = sub.price,
                            daysLeft = diffInDays,
                            timestamp = System.currentTimeMillis(),
                            isRead = false
                        )
                        // Wywołanie natywnego pusha w Androidzie
                        showSystemNotification(context, notification)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SUBTRACK_SCHEDULER", "Błąd podczas sprawdzania subskrypcji w tle: ${e.message}", e)
        }
    }

    // Konfiguracja i rejestracja cyklicznego WorkManagera (raz na 24 godziny)
    fun scheduleDailyNotificationCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 24L,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(5L, TimeUnit.MINUTES) // Pierwsze uruchomienie po 5 minutach od włączenia aplikacji
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SubTrackDailyCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}