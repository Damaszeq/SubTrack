package pl.lab2.subtrack

import android.app.Application
import pl.lab2.subtrack.data.AppContainer
import pl.lab2.subtrack.data.AppDataContainer

class SubTrackApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
