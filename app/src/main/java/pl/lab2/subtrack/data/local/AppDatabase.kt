package pl.lab2.subtrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.lab2.subtrack.data.local.dao.PaymentHistoryDao
import pl.lab2.subtrack.data.local.dao.SubscriptionDao
import pl.lab2.subtrack.data.local.dao.TagDao
import pl.lab2.subtrack.data.local.entities.PaymentHistory
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.entities.UserSubscription

@Database(
    entities = [
        UserSubscription::class,
        PaymentHistory::class,
        Tag::class,
        SubscriptionTagCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao
    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Tworzymy nową tabelę z dokładnie takimi samymi typami, jak w Twojej nowej encji UserSubscription
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `user_subscriptions_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `name` TEXT NOT NULL, 
                `planKey` TEXT NOT NULL, 
                `price` REAL NOT NULL, 
                `billingCycle` TEXT NOT NULL, 
                `startDate` INTEGER NOT NULL, 
                `nextPaymentDate` INTEGER NOT NULL, 
                `status` TEXT NOT NULL, 
                `isTrial` INTEGER NOT NULL, 
                `trialOption` TEXT NOT NULL, 
                `hasCustomReminders` INTEGER NOT NULL, 
                `notificationSetting` TEXT NOT NULL
            )
        """.trimIndent())

                // 2. Przepisujemy stare dane (stara kolumna `plan` trafia do nowej kolumny `planKey`)
                db.execSQL("""
            INSERT INTO `user_subscriptions_new` (
                `id`, `name`, `planKey`, `price`, `billingCycle`, `startDate`, 
                `nextPaymentDate`, `status`, `isTrial`, `trialOption`, `hasCustomReminders`, `notificationSetting`
            )
            SELECT 
                `id`, `name`, `plan`, `price`, `billingCycle`, `startDate`, 
                `nextPaymentDate`, `status`, `isTrial`, `trialOption`, `hasCustomReminders`, `notificationSetting` 
            FROM `user_subscriptions`
        """.trimIndent())

                // 3. Usuwamy starą tabelę
                db.execSQL("DROP TABLE `user_subscriptions`")

                // 4. Zmieniamy nazwę nowej tabeli na właściwą
                db.execSQL("ALTER TABLE `user_subscriptions_new` RENAME TO `user_subscriptions`")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "subtrack_database")
                    .addMigrations(MIGRATION_2_3) // POPRAWKA: Rejestrujemy naszą migrację
                    // Pozostawiamy fallback na wypadek innych, starych wersji,
                    // ale dla przejścia 2 -> 3 Room użyje teraz bezpiecznej MIGRATION_2_3
                    .fallbackToDestructiveMigrationFrom(1)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}