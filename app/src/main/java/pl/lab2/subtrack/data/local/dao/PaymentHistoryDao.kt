package pl.lab2.subtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.PaymentHistoryEntity
import pl.lab2.subtrack.data.local.entities.SubscriptionWithHistory

@Dao
interface PaymentHistoryDao {

    // 1. Wstawianie nowej płatności do historii
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentHistoryEntity)

    // 2. Pobieranie całej historii płatności dla konkretnej subskrypcji (sortowane od najnowszej)
    @Query("SELECT * FROM real_payment_history WHERE subscriptionId = :subId ORDER BY paymentDate DESC")
    fun getPaymentsForSubscription(subId: Long): Flow<List<PaymentHistoryEntity>>

    // 3. Pobieranie subskrypcji razem z jej historią
    @Transaction
    @Query("SELECT * FROM user_subscriptions WHERE id = :subId")
    fun getSubscriptionWithHistory(subId: Long): Flow<SubscriptionWithHistory?>

    // 4. Usunięcie konkretnego wpisu z historii (jeśli dasz użytkownikowi możliwość usuwania pojedynczych miesięcy)
    @Delete
    suspend fun deletePayment(payment: PaymentHistoryEntity)

    // 5. Czyszczenie całej historii dla danego suba
    @Query("SELECT * FROM real_payment_history")
    fun getAllPayments(): Flow<List<PaymentHistoryEntity>>
}