package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "real_payment_history",
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
data class PaymentHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subscriptionId: Long,
    val paymentDate: Long,
    val amountPaid: Double
)