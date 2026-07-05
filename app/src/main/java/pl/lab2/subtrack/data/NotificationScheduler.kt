package pl.lab2.subtrack.notification

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import pl.lab2.subtrack.data.NotificationWorker
import pl.lab2.subtrack.data.SettingsManager
import pl.lab2.subtrack.data.local.entities.PaymentHistory
import pl.lab2.subtrack.data.local.entities.NotificationHistory
import pl.lab2.subtrack.data.local.repositories.SubscriptionRepository
import pl.lab2.subtrack.data.local.repositories.PaymentRepository
import pl.lab2.subtrack.data.local.repositories.NotificationHistoryRepository
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.models.NotificationType
import pl.lab2.subtrack.toSubscription
import pl.lab2.subtrack.ui.showSystemNotification
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.Calendar
import java.util.Locale

class NotificationScheduler(
    private val context: Context,
    private val paymentRepository: PaymentRepository,
    private val notificationHistoryRepository: NotificationHistoryRepository
) {

    private val settingsManager = SettingsManager(context)
    private val plLocale = Locale("pl", "PL")

    suspend fun checkSubscriptionsAndNotify(repository: SubscriptionRepository) {
        try {
            val isGlobalEnabled = settingsManager.isNotificationsEnabledGlobal.first()
            if (!isGlobalEnabled) {
                android.util.Log.d("SUBTRACK_SCHEDULER", "Powiadomienia globalne są wyłączone w ustawieniach.")
                return
            }

            val preferredHours = settingsManager.globalReminderHours.first()
            val entitiesWithTags = repository.getActiveSubscriptionsWithTagsStream().first()

            for (subWithTagsEntity in entitiesWithTags) {
                val sub = subWithTagsEntity.toSubscription()
                val originalEntity = subWithTagsEntity.subscription

                if (sub.notificationSetting == "true") {
                    val todayCal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val subCal = Calendar.getInstance().apply {
                        timeInMillis = sub.nextPaymentDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val diffInMs = subCal.timeInMillis - todayCal.timeInMillis
                    var diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs).toInt()
                    val diffInHours = diffInDays * 24

                    // 1. SPRAWDZANIE I WYSYŁANIE AKTUALNYCH POWIADOMIEŃ
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

                        val notifTitle = "Nadchodzi płatność za ${sub.name}"
                        val notifMessage = if (sub.isTrial) {
                            "Twój okres próbny kończy się za $diffInDays dni! Zostanie pobrana opłata: ${String.format(plLocale, "%.2f", sub.price)} zł."
                        } else {
                            "Przypomnienie: Za $diffInDays dni pobierzemy z Twojego konta ${String.format(plLocale, "%.2f", sub.price)} zł."
                        }

                        val historyNotification = NotificationHistory(
                            subscriptionId = originalEntity.id,
                            serviceName = originalEntity.name,
                            title = notifTitle,
                            message = notifMessage,
                            timestamp = System.currentTimeMillis()
                        )

                        notificationHistoryRepository.insertNotification(historyNotification)
                        android.util.Log.d("SUBTRACK_NOTIF_LOG", "Zapisano powiadomienie dla ${originalEntity.name} do historii w bazie.")
                    }

                    // 2. AUTOMATYCZNE ODNAWIANIE ZALEGŁYCH PŁATNOŚCI (ZABEZPIECZONE PĘTLĄ WHILE)
                    var updatedNextPaymentDate = originalEntity.nextPaymentDate
                    var tempDiffInDays = diffInDays

                    while (tempDiffInDays < 0) {
                        val historyEntry = PaymentHistory(
                            subscriptionId = originalEntity.id,
                            serviceName = originalEntity.name,
                            planName = sub.plan,
                            price = originalEntity.price,
                            paymentDate = updatedNextPaymentDate
                        )

                        paymentRepository.insertPayment(historyEntry)
                        android.util.Log.d("SUBTRACK_HISTORY", "Automatycznie dodano zaległą płatność dla ${originalEntity.name} do historii.")

                        // Przesunięcie terminu o kolejny cykl rozliczeniowy
                        updatedNextPaymentDate = incrementPaymentDate(updatedNextPaymentDate, originalEntity.billingCycle)

                        // Ponowne przeliczenie różnicy dni dla pętli while
                        val nextSubCal = Calendar.getInstance().apply {
                            timeInMillis = updatedNextPaymentDate
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        tempDiffInDays = TimeUnit.MILLISECONDS.toDays(nextSubCal.timeInMillis - todayCal.timeInMillis).toInt()
                    }

                    // Zapisujemy nową datę do bazy tylko jeśli faktycznie nastąpiła zmiana cyklu
                    if (updatedNextPaymentDate != originalEntity.nextPaymentDate) {
                        val renewedSubscription = originalEntity.copy(
                            nextPaymentDate = updatedNextPaymentDate
                        )
                        repository.updateSubscription(renewedSubscription)
                        android.util.Log.d("SUBTRACK_AUTORENEW", "Przesunięto termin subskrypcji ${originalEntity.name} na dzień: $updatedNextPaymentDate")
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SUBTRACK_SCHEDULER", "Błąd podczas sprawdzania subskrypcji w tle: ${e.message}", e)
        }
    }

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
            .setInitialDelay(5L, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SubTrackDailyCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun incrementPaymentDate(currentNextPayment: Long, billingCycle: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentNextPayment
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (billingCycle.lowercase(Locale.ROOT)) {
            "monthly", "miesięczny", "miesięcznie" -> calendar.add(Calendar.MONTH, 1)
            "yearly", "roczny", "rocznie", "rok", "year" -> calendar.add(Calendar.YEAR, 1)
            "weekly", "tygodniowy", "tygodniowo", "tydzień", "week" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "kwartał", "quarter" -> calendar.add(Calendar.MONTH, 3)
            else -> calendar.add(Calendar.MONTH, 1)
        }
        return calendar.timeInMillis
    }
}