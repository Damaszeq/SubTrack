package pl.lab2.subtrack.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SubscriptionWithHistory(
    @Embedded
    val subscription: UserSubscription,

    @Relation(
        parentColumn = "id",
        entityColumn = "subscriptionId"
    )
    val paymentHistory: List<PaymentHistoryEntity>
)