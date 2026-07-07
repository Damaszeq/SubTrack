package pl.lab2.subtrack.data.local.repositories

import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.dao.PaymentHistoryDao
import pl.lab2.subtrack.data.local.entities.PaymentHistoryEntity
import pl.lab2.subtrack.data.local.entities.SubscriptionWithHistory

/**
 * Repository that provides insert and retrieve of [PaymentHistoryEntity] from a given data source.
 */
interface PaymentRepository {
    /**
     * Retrieve all the payments from the given data source.
     */
    fun getAllPaymentsStream(): Flow<List<PaymentHistoryEntity>>

    /**
     * Retrieve payments for a specific subscription from the given data source.
     */
    fun getPaymentsForSubscriptionStream(subscriptionId: Long): Flow<List<PaymentHistoryEntity>>

    /**
     * Retrieve a subscription along with its full payment history.
     */
    fun getSubscriptionWithHistoryStream(subscriptionId: Long): Flow<SubscriptionWithHistory?>

    /**
     * Insert payment in the data source.
     */
    suspend fun insertPayment(payment: PaymentHistoryEntity)
}

class OfflinePaymentRepository(private val paymentHistoryDao: PaymentHistoryDao) : PaymentRepository {
    override fun getAllPaymentsStream(): Flow<List<PaymentHistoryEntity>> =
        paymentHistoryDao.getAllPayments()

    override fun getPaymentsForSubscriptionStream(subscriptionId: Long): Flow<List<PaymentHistoryEntity>> =
        paymentHistoryDao.getPaymentsForSubscription(subscriptionId)

    override fun getSubscriptionWithHistoryStream(subscriptionId: Long): Flow<SubscriptionWithHistory?> =
        paymentHistoryDao.getSubscriptionWithHistory(subscriptionId)

    override suspend fun insertPayment(payment: PaymentHistoryEntity) =
        paymentHistoryDao.insertPayment(payment)
}