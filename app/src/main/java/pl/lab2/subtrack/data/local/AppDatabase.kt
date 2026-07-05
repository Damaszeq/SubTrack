package pl.lab2.subtrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.lab2.subtrack.data.local.dao.PaymentHistoryDao
import pl.lab2.subtrack.data.local.dao.SubscriptionDao
import pl.lab2.subtrack.data.local.dao.TagDao
import pl.lab2.subtrack.data.local.entities.PaymentHistory
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.entities.UserSubscription
import pl.lab2.subtrack.data.local.dao.NotificationHistoryDao
import pl.lab2.subtrack.data.local.entities.NotificationHistory

@Database(
    entities = [
        UserSubscription::class,
        PaymentHistory::class,
        Tag::class,
        SubscriptionTagCrossRef::class,
        NotificationHistory::class
    ],
    version = 5, //Tabela powiadomien
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao
    abstract fun tagDao(): TagDao

    abstract fun notificationHistoryDao(): NotificationHistoryDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "subtrack_database")
                    // Destruktywna migracja automatycznie wyczyści i postawi bazę na nowo bez crashy
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}