package pl.lab2.subtrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ============================================================================
// DELEGAT DOSTĘPU DO DATASTORE
// ============================================================================

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// ============================================================================
// ZARZĄDZANIE USTAWIENIAMI APLIKACJI
// ============================================================================

class SettingsManager(private val context: Context) {

    companion object {
        val GLOBAL_NOTIFICATIONS_KEY = booleanPreferencesKey("global_notifications_enabled")
        val REMINDER_HOURS_KEY = stringSetPreferencesKey("global_reminder_hours")
    }

    // ------------------------------------------------------------------------
    // ODCZYT USTAWIEŃ (STRUMIENIE)
    // ------------------------------------------------------------------------

    val isNotificationsEnabledGlobal: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[GLOBAL_NOTIFICATIONS_KEY] ?: true
        }

    val globalReminderHours: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            val stringSet = preferences[REMINDER_HOURS_KEY] ?: setOf("24")
            stringSet.mapNotNull { it.toIntOrNull() }.toSet()
        }

    // ------------------------------------------------------------------------
    // ZAPIS USTAWIEŃ
    // ------------------------------------------------------------------------

    suspend fun setGlobalNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GLOBAL_NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setGlobalReminderHours(hours: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_HOURS_KEY] = hours.map { it.toString() }.toSet()
        }
    }
}