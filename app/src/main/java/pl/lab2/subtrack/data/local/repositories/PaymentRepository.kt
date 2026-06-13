package pl.lab2.subtrack.data.local.repositories

import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.dao.PaymentHistoryDao
import pl.lab2.subtrack.data.local.entities.PaymentHistory

/**
 * Repository that provides insert and retrieve of [PaymentHistory] from a given data source.
 */
interface PaymentRepository {
    /**
     * Retrieve all the payments from the the given data source.
     */
    fun getAllPaymentsStream(): Flow<List<PaymentHistory>>

    /**
     * Retrieve payments for a specific subscription from the given data source.
     */
    fun getPaymentsForSubscriptionStream(subscriptionId: Long): Flow<List<PaymentHistory>>

    /**
     * Retrieve total spending in a range from the given data source.
     */
    fun getTotalSpendingInRangeStream(startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Insert payment in the data source
     */
    suspend fun insertPayment(payment: PaymentHistory): Long
}

class OfflinePaymentRepository(private val paymentHistoryDao: PaymentHistoryDao) : PaymentRepository {
    override fun getAllPaymentsStream(): Flow<List<PaymentHistory>> = 
        paymentHistoryDao.getAllPayments()

    override fun getPaymentsForSubscriptionStream(subscriptionId: Long): Flow<List<PaymentHistory>> = 
        paymentHistoryDao.getPaymentsForSubscription(subscriptionId)

    override fun getTotalSpendingInRangeStream(startDate: Long, endDate: Long): Flow<Double?> = 
        paymentHistoryDao.getTotalSpendingInRange(startDate, endDate)

    override suspend fun insertPayment(payment: PaymentHistory): Long = 
        paymentHistoryDao.insertPayment(payment)
}
