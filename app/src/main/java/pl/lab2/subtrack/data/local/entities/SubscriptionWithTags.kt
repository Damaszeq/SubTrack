package pl.lab2.subtrack.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SubscriptionWithTags(
    @Embedded val subscription: UserSubscription,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SubscriptionTagCrossRef::class,
            parentColumn = "subscriptionId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)
