package pl.lab2.subtrack.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.SubscriptionWithTags
import pl.lab2.subtrack.data.local.entities.UserSubscription

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: UserSubscription): Long

    @Update
    suspend fun updateSubscription(subscription: UserSubscription)

    // Ta metoda posłuży do PERMANENTNEGO usuwania pomyłkowych wpisów
    @Delete
    suspend fun deleteSubscription(subscription: UserSubscription)

    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE id = :id")
    fun getSubscriptionWithTagsFlow(id: Long): Flow<SubscriptionWithTags?>

    // Pobiera TYLKO aktywne subskrypcje (wyklucza martwe i zarchiwizowane)
    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE status != 'DEAD' AND status != 'ARCHIVED'")
    fun getActiveSubscriptionsWithTags(): Flow<List<SubscriptionWithTags>>

    // NOWE ZAPYTANIE: Pobiera wyłącznie subskrypcje zarchiwizowane, sortując od najwcześniej zakończonych
    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE status = 'ARCHIVED' ORDER BY endDate DESC")
    fun getArchivedSubscriptionsWithTags(): Flow<List<SubscriptionWithTags>>

    // NOWA METODA: Logiczna archiwizacja subskrypcji - zmienia status i ustawia datę końca
    @Query("UPDATE user_subscriptions SET status = 'ARCHIVED', endDate = :endDate WHERE id = :id")
    suspend fun archiveSubscription(id: Long, endDate: Long)

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
    suspend fun updateSubscriptionWithTags(subscription: UserSubscription, tags: List<pl.lab2.subtrack.data.local.entities.Tag>, tagDao: TagDao) {
        updateSubscription(subscription)
        deleteTagsForSubscription(subscription.id)
        tags.forEach { tag ->
            val existingTag = tagDao.getTagByName(tag.name)
            val tagId = existingTag?.id ?: tagDao.insertTag(tag)
            insertSubscriptionTagCrossRef(SubscriptionTagCrossRef(subscription.id, tagId))
        }
    }

    @Transaction
    suspend fun insertSubscriptionWithTags(subscription: UserSubscription, tags: List<pl.lab2.subtrack.data.local.entities.Tag>, tagDao: TagDao): Long {
        val subId = insertSubscription(subscription)
        tags.forEach { tag ->
            val existingTag = tagDao.getTagByName(tag.name)
            val tagId = existingTag?.id ?: tagDao.insertTag(tag)
            insertSubscriptionTagCrossRef(SubscriptionTagCrossRef(subId, tagId))
        }
        return subId
    }
}