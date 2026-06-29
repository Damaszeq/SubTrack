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

            // Pobieramy pełne encje z relacjami, żeby mieć dostęp do obiektów bazodanowych do zapisu
            val entitiesWithTags = repository.getActiveSubscriptionsWithTagsStream().first()
            val currentTime = System.currentTimeMillis()

            for (subWithTagsEntity in entitiesWithTags) {
                val sub = subWithTagsEntity.toSubscription()

                if (sub.notificationSetting == "true") {
                    // Sprowadzamy czas bieżący do północy dnia dzisiejszego
                    val todayCal = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }

                    // Sprowadzamy datę płatności do północy
                    val subCal = java.util.Calendar.getInstance().apply {
                        timeInMillis = sub.nextPaymentDate
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }

                    val diffInMs = subCal.timeInMillis - todayCal.timeInMillis
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs).toInt()
                    val diffInHours = diffInDays * 24

                    // 3. DYNAMICZNY WARUNEK: Sprawdzamy, czy czas do płatności pokrywa się z wyborem użytkownika
                    val isTimeMatched = preferredHours.any { hour ->
                        diffInHours == hour || (hour == 24 && diffInDays == 1)
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
                        showSystemNotification(context, notification)
                    }

                    // 4. AUTOMATYCZNE ODNAWIANIE DATY PŁATNOŚCI
                    // Teraz diffInDays wyniesie dokładnie 0, jeśli termin przypada na dzisiaj.
                    // Warunek < 0 wykona się dopierojutro (gdy diffInDays wyniesie -1).
                    if (diffInDays < 0) {
                        val originalEntity = subWithTagsEntity.subscription
                        val updatedNextPaymentDate = incrementPaymentDate(originalEntity.nextPaymentDate, originalEntity.billingCycle)

                        val renewedSubscription = originalEntity.copy(
                            nextPaymentDate = updatedNextPaymentDate
                        )

                        repository.updateSubscription(renewedSubscription)

                        android.util.Log.d(
                            "SUBTRACK_AUTORENEW",
                            "Subskrypcja ${originalEntity.name} minęła ($diffInDays dni). Automatyczne przesunięcie daty."
                        )
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

    private fun incrementPaymentDate(currentNextPayment: Long, billingCycle: String): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = currentNextPayment

        val currentTime = System.currentTimeMillis()

        // Przesuwamy datę do przodu tak długo, aż znajdzie się w przyszłości
        while (calendar.timeInMillis <= currentTime) {
            when (billingCycle.lowercase(java.util.Locale.ROOT)) {
                "monthly", "miesięczny", "miesięcznie" -> calendar.add(java.util.Calendar.MONTH, 1)
                "yearly", "roczny", "rocznie", "rok", "year" -> calendar.add(java.util.Calendar.YEAR, 1)
                "weekly", "tygodniowy", "tygodniowo", "tydzień", "week" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                "kwartał", "quarter" -> calendar.add(java.util.Calendar.MONTH, 3)
                else -> calendar.add(java.util.Calendar.MONTH, 1)
            }
        }
        return calendar.timeInMillis
    }
}