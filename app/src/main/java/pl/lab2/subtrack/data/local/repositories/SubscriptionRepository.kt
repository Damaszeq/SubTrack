package pl.lab2.subtrack.data.local.repositories

import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.dao.SubscriptionDao
import pl.lab2.subtrack.data.local.dao.TagDao
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.SubscriptionWithTags
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.entities.UserSubscription

/**
 * Repository that provides insert, update, delete, and retrieve of [UserSubscription] from a given data source.
 */
interface SubscriptionRepository {
    /**
     * Retrieve all the subscriptions with their tags from the the given data source.
     */
    fun getAllSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>>

    /**
     * Retrieve all the active subscriptions with their tags from the the given data source.
     */
    fun getActiveSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>>

    /**
     * Retrieve all the archived subscriptions with their tags from the the given data source.
     */
    fun getArchivedSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>>

    /**
     * Logically archive a subscription by changing its status and setting the end date.
     */
    suspend fun archiveSubscription(id: Long, endDate: Long)

    /**
     * Retrieve a subscription with tags from the given data source that matches with the [id].
     */
    fun getSubscriptionWithTagsStream(id: Long): Flow<SubscriptionWithTags?>

    /**
     * Insert subscription in the data source
     */
    suspend fun insertSubscription(subscription: UserSubscription): Long

    /**
     * Delete subscription from the data source (Permanent deletion)
     */
    suspend fun deleteSubscription(subscription: UserSubscription)

    /**
     * Update subscription in the data source
     */
    suspend fun updateSubscription(subscription: UserSubscription)

    /**
     * Insert subscription tag cross ref in the data source
     */
    suspend fun insertSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef)

    /**
     * Delete subscription tag cross ref from the data source
     */
    suspend fun deleteSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef)

    /**
     * Delete all tag associations for a subscription
     */
    suspend fun deleteTagsForSubscription(subscriptionId: Long)

    /**
     * Atomic insert of subscription and its tags
     */
    suspend fun insertSubscriptionWithTags(subscription: UserSubscription, tags: List<Tag>, tagDao: pl.lab2.subtrack.data.local.dao.TagDao): Long

    /**
     * Atomic update of subscription and its tags
     */
    suspend fun updateSubscriptionWithTags(subscription: UserSubscription, tags: List<Tag>, tagDao: pl.lab2.subtrack.data.local.dao.TagDao)
}

class OfflineSubscriptionRepository(private val subscriptionDao: SubscriptionDao) : SubscriptionRepository {
    override fun getAllSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>> =
        subscriptionDao.getAllSubscriptionsWithTags()

    override fun getActiveSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>> =
        subscriptionDao.getActiveSubscriptionsWithTags()

    override fun getArchivedSubscriptionsWithTagsStream(): Flow<List<SubscriptionWithTags>> =
        subscriptionDao.getArchivedSubscriptionsWithTags()

    override suspend fun archiveSubscription(id: Long, endDate: Long) =
        subscriptionDao.archiveSubscription(id, endDate)

    override fun getSubscriptionWithTagsStream(id: Long): Flow<SubscriptionWithTags?> =
        subscriptionDao.getSubscriptionWithTagsFlow(id)

    override suspend fun insertSubscription(subscription: UserSubscription): Long =
        subscriptionDao.insertSubscription(subscription)

    override suspend fun deleteSubscription(subscription: UserSubscription) =
        subscriptionDao.deleteSubscription(subscription)

    override suspend fun updateSubscription(subscription: UserSubscription) =
        subscriptionDao.updateSubscription(subscription)

    override suspend fun insertSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef) =
        subscriptionDao.insertSubscriptionTagCrossRef(crossRef)

    override suspend fun deleteSubscriptionTagCrossRef(crossRef: SubscriptionTagCrossRef) =
        subscriptionDao.deleteSubscriptionTagCrossRef(crossRef)

    override suspend fun deleteTagsForSubscription(subscriptionId: Long) =
        subscriptionDao.deleteTagsForSubscription(subscriptionId)

    override suspend fun insertSubscriptionWithTags(subscription: UserSubscription, tags: List<Tag>, tagDao: TagDao): Long =
        subscriptionDao.insertSubscriptionWithTags(subscription, tags, tagDao)

    override suspend fun updateSubscriptionWithTags(subscription: UserSubscription, tags: List<Tag>, tagDao: TagDao) =
        subscriptionDao.updateSubscriptionWithTags(subscription, tags, tagDao)
}