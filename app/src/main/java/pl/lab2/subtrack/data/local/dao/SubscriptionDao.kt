package pl.lab2.subtrack.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.SubscriptionWithTags
import pl.lab2.subtrack.data.local.entities.UserSubscription
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: UserSubscription): Long

    @Update
    suspend fun updateSubscription(subscription: UserSubscription)

    @Delete
    suspend fun deleteSubscription(subscription: UserSubscription)

    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE id = :id")
    fun getSubscriptionWithTagsFlow(id: Long): Flow<SubscriptionWithTags?>

    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE status != 'DEAD'")
    fun getActiveSubscriptionsWithTags(): Flow<List<SubscriptionWithTags>>

    @Transaction
    @Query("SELECT * FROM user_subscriptions")
    fun getAllSubscriptionsWithTags(): Flow<List<SubscriptionWithTags>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef)

    @Delete
    suspend fun deleteSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef)

    @Query("DELETE FROM subscription_tag_cross_ref WHERE subscriptionId = :subscriptionId")
    suspend fun deleteTagsForSubscription(subscriptionId: Long)

    @Transaction
    suspend fun updateSubscriptionWithTags(subscription: pl.lab2.subtrack.data.local.entities.UserSubscription, tags: List<pl.lab2.subtrack.data.local.entities.Tag>, tagDao: pl.lab2.subtrack.data.local.dao.TagDao) {
        updateSubscription(subscription)
        deleteTagsForSubscription(subscription.id)
        tags.forEach { tag ->
            val existingTag = tagDao.getTagByName(tag.name)
            val tagId = existingTag?.id ?: tagDao.insertTag(tag)
            insertSubscriptionTagCrossRef(pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef(subscription.id, tagId))
        }
    }

    @Transaction
    suspend fun insertSubscriptionWithTags(subscription: pl.lab2.subtrack.data.local.entities.UserSubscription, tags: List<pl.lab2.subtrack.data.local.entities.Tag>, tagDao: pl.lab2.subtrack.data.local.dao.TagDao): Long {
        val subId = insertSubscription(subscription)
        tags.forEach { tag ->
            val existingTag = tagDao.getTagByName(tag.name)
            val tagId = existingTag?.id ?: tagDao.insertTag(tag)
            insertSubscriptionTagCrossRef(pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef(subId, tagId))
        }
        return subId
    }
}
