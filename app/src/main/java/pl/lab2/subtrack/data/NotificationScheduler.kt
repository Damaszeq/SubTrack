package pl.lab2.subtrack.data

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import pl.lab2.subtrack.data.local.repositories.SubscriptionRepository
import pl.lab2.subtrack.models.NotificationItem
import pl.lab2.subtrack.models.NotificationType
import pl.lab2.subtrack.toSubscription
import pl.lab2.subtrack.ui.showSystemNotification
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    suspend fun checkSubscriptionsAndNotify(repository: SubscriptionRepository) {
        try {
            val entitiesWithTags = repository.getActiveSubscriptionsWithTagsStream().first()
            val activeSubs = entitiesWithTags.map { it.toSubscription() }
            val currentTime = System.currentTimeMillis()

            for (sub in activeSubs) {
                if (sub.notificationSetting == "true") {
                    val diffInMs = sub.nextPaymentDate - currentTime
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs).toInt()

                    if (diffInDays in 0..3) {
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
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SUBTRACK_SCHEDULER", "Błąd sprawdzania terminów w tle: ${e.message}", e)
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
}