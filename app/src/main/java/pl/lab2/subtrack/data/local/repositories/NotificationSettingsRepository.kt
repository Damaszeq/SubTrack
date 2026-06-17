package pl.lab2.subtrack.data.local.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.lab2.subtrack.model.GlobalNotificationSettings

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "global_settings")

interface NotificationSettingsRepository {
    val globalSettingsStream: Flow<GlobalNotificationSettings>
    suspend fun updateSettings(settings: GlobalNotificationSettings)
}

class OfflineNotificationSettingsRepository(private val context: Context) : NotificationSettingsRepository {

    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("global_notifications_enabled")
        val REMINDER_HOURS = stringPreferencesKey("global_reminder_hours")
        val NOTIFICATION_TIME = stringPreferencesKey("global_notification_time")
    }

    override val globalSettingsStream: Flow<GlobalNotificationSettings> = context.dataStore.data
        .map { preferences ->
            val isEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            val hoursString = preferences[PreferencesKeys.REMINDER_HOURS] ?: "24,72"
            val time = preferences[PreferencesKeys.NOTIFICATION_TIME] ?: "10:00"

            // Konwersja String "24,72" -> List<Int>
            val hoursList = hoursString.split(",")
                .mapNotNull { it.trim().toIntOrNull() }

            GlobalNotificationSettings(
                isEnabled = isEnabled,
                subscriptionReminderHours = hoursList,
                notificationTime = time
            )
        }

    override suspend fun updateSettings(settings: GlobalNotificationSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = settings.isEnabled
            preferences[PreferencesKeys.REMINDER_HOURS] = settings.subscriptionReminderHours.joinToString(",")
            preferences[PreferencesKeys.NOTIFICATION_TIME] = settings.notificationTime
        }
    }
}