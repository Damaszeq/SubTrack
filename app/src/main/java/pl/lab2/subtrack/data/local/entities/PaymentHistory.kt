package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_history",
    foreignKeys = [
        ForeignKey(
            entity = UserSubscription::class,
            parentColumns = ["id"],
            childColumns = ["subscriptionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subscriptionId"])]
)
data class PaymentHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subscriptionId: Long,
    val serviceName: String, // Kept for historical record in case subscription is deleted or changed
    val planName: String,
    val price: Double,
    val paymentDate: Long // Timestamp
)
