package pl.lab2.subtrack

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.lab2.subtrack.data.local.AppDatabase
import pl.lab2.subtrack.data.local.dao.SubscriptionDao
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus
import pl.lab2.subtrack.data.local.entities.SubscriptionTagCrossRef
import pl.lab2.subtrack.data.local.entities.Tag
import pl.lab2.subtrack.data.local.entities.UserSubscription
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var subscriptionDao: SubscriptionDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        subscriptionDao = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val subscription = UserSubscription(
            name = "Netflix",
            planName = "Premium",
            price = 54.0,
            nextPaymentDate = System.currentTimeMillis(),
            status = SubscriptionStatus.ACTIVE
        )
        val id = subscriptionDao.insertSubscription(subscription)
        
        val allSubscriptions = subscriptionDao.getAllSubscriptionsWithTags().first()
        assertEquals(1, allSubscriptions.size)
        assertEquals("Netflix", allSubscriptions[0].subscription.name)
        assertEquals(id, allSubscriptions[0].subscription.id)
    }

    @Test
    @Throws(Exception::class)
    fun testManyToManyTags() = runBlocking {
        val subId = subscriptionDao.insertSubscription(UserSubscription(
            name = "Spotify",
            planName = "Family",
            price = 29.99,
            nextPaymentDate = System.currentTimeMillis(),
            status = SubscriptionStatus.ACTIVE
        ))

        val tagDao = db.tagDao()
        val tagId1 = tagDao.insertTag(Tag(name = "Music"))
        val tagId2 = tagDao.insertTag(Tag(name = "Streaming"))

        subscriptionDao.insertSubscriptionTagCrossRef(SubscriptionTagCrossRef(subId, tagId1))
        subscriptionDao.insertSubscriptionTagCrossRef(SubscriptionTagCrossRef(subId, tagId2))

        val subWithTags = subscriptionDao.getSubscriptionWithTags(subId)
        assertEquals(2, subWithTags?.tags?.size)
        val tagNames = subWithTags?.tags?.map { it.name } ?: emptyList()
        assert(tagNames.contains("Music"))
        assert(tagNames.contains("Streaming"))
    }
}
