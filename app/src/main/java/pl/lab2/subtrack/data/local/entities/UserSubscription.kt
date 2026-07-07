package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SubscriptionStatus {
    ACTIVE, SUSPENDED, DEAD, ARCHIVED
}

@Entity(tableName = "user_subscriptions")
data class UserSubscription(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val planKey: String,
    val price: Double,          // Aktualna cena (w czasie triala: cena okresu próbnego np. 0.00 lub 1.00)
    val billingCycle: String,   // Cykl rozliczeniowy (np. "Miesiąc")
    val startDate: Long,
    val nextPaymentDate: Long,  // Timestamp kolejnej płatności / końca triala
    val status: SubscriptionStatus,
    val isTrial: Boolean = false,
    val trialOption: String = "",
    val hasCustomReminders: Boolean = false,
    val notificationSetting: String = "true",
    val endDate: Long? = null,
    val trialEndDate: Long? = null,

    // NOWE POLE: Cena regularna, która zacznie obowiązywać po zakończeniu okresu próbnego
    val regularPrice: Double = 0.0
)