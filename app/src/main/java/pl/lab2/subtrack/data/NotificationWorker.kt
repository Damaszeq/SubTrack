package pl.lab2.subtrack.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.lab2.subtrack.SubTrackApplication
import pl.lab2.subtrack.notification.NotificationScheduler

// ============================================================================
// WORKER ZADANIA W TLE
// ============================================================================

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as SubTrackApplication

            val scheduler = NotificationScheduler(
                context = applicationContext,
                paymentRepository = app.container.paymentRepository,
                notificationHistoryRepository = app.container.notificationHistoryRepository
            )

            scheduler.checkSubscriptionsAndNotify(app.container.subscriptionRepository)
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SUBTRACK_WORKER", "Błąd wykonania zadania Workera: ${e.message}", e)
            Result.failure()
        }
    }
}