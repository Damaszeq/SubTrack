package pl.lab2.subtrack.data

import android.content.Context
import pl.lab2.subtrack.data.local.AppDatabase
import pl.lab2.subtrack.data.local.repositories.*

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val subscriptionRepository: SubscriptionRepository
    val paymentRepository: PaymentRepository
    val tagRepository: TagRepository
    val notificationSettingsRepository: NotificationSettingsRepository // NOWE
    val database: AppDatabase
}

/**
 * [AppContainer] implementation that provides instance of [OfflineSubscriptionRepository],
 * [OfflinePaymentRepository], and [OfflineTagRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    /**
     * Implementation for [SubscriptionRepository]
     */
    override val subscriptionRepository: SubscriptionRepository by lazy {
        OfflineSubscriptionRepository(database.subscriptionDao())
    }

    /**
     * Implementation for [PaymentRepository]
     */
    override val paymentRepository: PaymentRepository by lazy {
        OfflinePaymentRepository(database.paymentHistoryDao())
    }

    /**
     * Implementation for [TagRepository]
     */
    override val tagRepository: TagRepository by lazy {
        OfflineTagRepository(database.tagDao())
    }

    /**
     * Implementation for [NotificationSettingsRepository]
     */
    override val notificationSettingsRepository: NotificationSettingsRepository by lazy {
        OfflineNotificationSettingsRepository(context)
    }
}