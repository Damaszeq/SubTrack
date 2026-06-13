package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SubscriptionStatus {
    ACTIVE, SUSPENDED, DEAD
}

@Entity(tableName = "user_subscriptions")
data class UserSubscription(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val planName: String,
    val price: Double,
    val billingCycle: String,
    val startDate: Long,
    val nextPaymentDate: Long, // Timestamp
    val status: SubscriptionStatus,
    val isTrial: Boolean = false,
    val trialOption: String = ""
)
