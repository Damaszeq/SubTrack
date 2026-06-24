package pl.lab2.subtrack.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.lab2.subtrack.SubTrackApplication
import pl.lab2.subtrack.data.SettingsManager


/**
 * Provides Factory to create instance of ViewModel for the entire SubTrack app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this.subTrackApplication()

            SubscriptionViewModel(
                subscriptionRepository = app.container.subscriptionRepository,
                tagRepository = app.container.tagRepository,
                tagDao = app.container.database.tagDao(),
                settingsManager = SettingsManager(app.applicationContext)
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [SubTrackApplication].
 */
fun CreationExtras.subTrackApplication(): SubTrackApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SubTrackApplication)
