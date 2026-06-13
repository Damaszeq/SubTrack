package pl.lab2.subtrack.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.PaymentHistory

@Dao
interface PaymentHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentHistory): Long

    @Query("SELECT * FROM payment_history WHERE subscriptionId = :subscriptionId ORDER BY paymentDate DESC")
    fun getPaymentsForSubscription(subscriptionId: Long): Flow<List<PaymentHistory>>

    @Query("SELECT * FROM payment_history ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentHistory>>

    @Query("SELECT SUM(price) FROM payment_history WHERE paymentDate >= :startDate AND paymentDate <= :endDate")
    fun getTotalSpendingInRange(startDate: Long, endDate: Long): Flow<Double?>
}
