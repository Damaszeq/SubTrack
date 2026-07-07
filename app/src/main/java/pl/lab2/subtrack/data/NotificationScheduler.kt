package pl.lab2.subtrack.notification

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import pl.lab2.subtrack.data.NotificationWorker
import pl.lab2.subtrack.data.SettingsManager
import pl.lab2.subtrack.data.local.entities.PaymentHistoryEntity
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
                var originalEntity = subWithTagsEntity.subscription // Usunięto 'val', aby móc modyfikować stan w locie

                val isNotifEnabled = !sub.notificationSetting.equals("Wyłączone", ignoreCase = true)

                if (isNotifEnabled) {
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
                            type = if (originalEntity.isTrial) NotificationType.TRIAL_EXPIRING else NotificationType.PAYMENT_REMINDER,
                            priceTriggered = originalEntity.price,
                            daysLeft = diffInDays,
                            timestamp = System.currentTimeMillis(),
                            isRead = false
                        )

                        showSystemNotification(context, notification)

                        val notifTitle = "Nadchodzi płatność za ${sub.name}"
                        val notifMessage = if (originalEntity.isTrial) {
                            "Twój okres próbny kończy się za $diffInDays dni! Zostanie pobrana opłata: ${String.format(plLocale, "%.2f", originalEntity.price)} zł."
                        } else {
                            "Przypomnienie: Za $diffInDays dni pobierzemy z Twojego konta ${String.format(plLocale, "%.2f", originalEntity.price)} zł."
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

                    // 2. AUTOMATYCZNE ODNAWIANIE ZALEGŁYCH PŁATNOŚCI Z OBSŁUGĄ KONCA TRIALU
                    var updatedNextPaymentDate = originalEntity.nextPaymentDate
                    var tempDiffInDays = diffInDays

                    // Flagi pomocnicze do śledzenia zmian struktury subskrypcji w pętli wielu okresów wstecz
                    var currentIsTrial = originalEntity.isTrial
                    var currentPrice = originalEntity.price

                    while (tempDiffInDays < 0) {
                        // POPRAWKA: Jeśli usługa była oznaczona jako trial, to pierwsze odnowienie (koniec triala)
                        // powinno zarejestrować pobranie ceny regularnej, a usługa staje się płatna.
                        val amountToRegister = if (currentIsTrial) {
                            originalEntity.regularPrice
                        } else {
                            currentPrice
                        }

                        val historyEntry = PaymentHistoryEntity(
                            subscriptionId = originalEntity.id,
                            paymentDate = updatedNextPaymentDate,
                            amountPaid = amountToRegister
                        )

                        paymentRepository.insertPayment(historyEntry)
                        android.util.Log.d("SUBTRACK_HISTORY", "Automatycznie dodano płatność (kwota: $amountToRegister zł) dla ID: ${originalEntity.id}.")

                        // Po przejściu pierwszego zaległego okresu próbnego, zmieniamy lokalny stan flag na potrzeby kolejnych iteracji pętli
                        if (currentIsTrial) {
                            currentIsTrial = false
                            currentPrice = originalEntity.regularPrice
                        }

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

                    // Zapisujemy nową datę oraz zaktualizowany status okresu próbnego do bazy
                    if (updatedNextPaymentDate != originalEntity.nextPaymentDate) {
                        val renewedSubscription = originalEntity.copy(
                            nextPaymentDate = updatedNextPaymentDate,
                            isTrial = currentIsTrial, // Przypisanie nowej wartości (false)
                            price = currentPrice     // Nadpisanie podstawowej stawki ceną regularną
                        )
                        repository.updateSubscription(renewedSubscription)
                        android.util.Log.d("SUBTRACK_AUTORENEW", "Zakończono proces odnowienia ${originalEntity.name}. Nowa data płatności: $updatedNextPaymentDate, isTrial: $currentIsTrial, Aktualna cena cyklu: $currentPrice zł")
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