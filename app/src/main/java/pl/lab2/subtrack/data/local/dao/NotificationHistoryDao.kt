package pl.lab2.subtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.NotificationHistory

@Dao
interface NotificationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationHistory)

    // Pobieramy historię od najnowszych do najstarszych
    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationHistory>>

    // Opcjonalne: Usuwanie konkretnego wpisu
    @Query("DELETE FROM notification_history WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    // Wyczyszczenie całej historii z poziomu UI
    @Query("DELETE FROM notification_history")
    suspend fun clearAllHistory()

    @Update
    suspend fun update(notification: NotificationHistory)
}