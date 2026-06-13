package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "subscription_tag_cross_ref",
    primaryKeys = ["subscriptionId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = UserSubscription::class,
            parentColumns = ["id"],
            childColumns = ["subscriptionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subscriptionId"]),
        Index(value = ["tagId"])
    ]
)
data class SubscriptionTagCrossRef(
    val subscriptionId: Long,
    val tagId: Long
)
