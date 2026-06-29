package pl.lab2.subtrack.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pl.lab2.subtrack.notification.NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Sprawdzamy, czy przyszedł sygnał o włączeniu telefonu lub aktualizacji aplikacji
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            android.util.Log.d("SUBTRACK_BOOT", "Telefon uruchomiony lub aplikacja zaktualizowana. Rejestruję WorkManager...")

            // Inicjalizujemy scheduler i wrzucamy zadanie sprawdzania subskrypcji do kolejki systemu
            val scheduler = NotificationScheduler(context)
            scheduler.scheduleDailyNotificationCheck()
        }
    }
}