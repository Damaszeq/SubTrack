package pl.lab2.subtrack.data


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.lab2.subtrack.notification.NotificationScheduler
import pl.lab2.subtrack.ui.AppViewModelProvider

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        android.util.Log.d("SUBTRACK_WORKER", "Uruchomiono cykliczne sprawdzanie subskrypcji w tle...")

        try {
            // Ponieważ Worker nie ma dostępu do ViewModelu, pobieramy repozytorium bezpośrednio
            // z Twojego kontenera aplikacji (tak samo, jak robi to AppViewModelProvider dla ViewModeli)
            val application = applicationContext as pl.lab2.subtrack.SubTrackApplication // Upewnij się, że nazwa klasy Application się zgadza
            val repository = application.container.subscriptionRepository // Dostosuj ścieżkę do swojego kontenera (np. application.container...)

            val scheduler = NotificationScheduler(applicationContext)
            scheduler.checkSubscriptionsAndNotify(repository)

            return Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SUBTRACK_WORKER", "Błąd podczas wykonywania zadania w tle: ${e.message}", e)
            return Result.failure()
        }
    }
}