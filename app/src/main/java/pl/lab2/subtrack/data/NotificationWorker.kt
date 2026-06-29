package pl.lab2.subtrack.data


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.lab2.subtrack.SubTrackApplication
import pl.lab2.subtrack.notification.NotificationScheduler
import pl.lab2.subtrack.ui.AppViewModelProvider

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as SubTrackApplication

            // Przekazujemy oba repozytoria
            val scheduler = NotificationScheduler(
                context = applicationContext,
                paymentRepository = app.container.paymentRepository // nazwa repozytorium z Twojego AppContainer
            )

            scheduler.checkSubscriptionsAndNotify(app.container.subscriptionRepository)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}