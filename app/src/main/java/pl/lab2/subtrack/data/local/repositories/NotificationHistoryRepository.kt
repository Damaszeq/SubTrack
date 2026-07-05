package pl.lab2.subtrack.data.local.repositories

import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.dao.NotificationHistoryDao
import pl.lab2.subtrack.data.local.entities.NotificationHistory

class NotificationHistoryRepository(private val notificationHistoryDao: NotificationHistoryDao) {

    fun getAllNotifications(): Flow<List<NotificationHistory>> =
        notificationHistoryDao.getAllNotifications()

    suspend fun insertNotification(notification: NotificationHistory) {
        notificationHistoryDao.insertNotification(notification)
    }

    suspend fun deleteNotificationById(id: Long) {
        notificationHistoryDao.deleteNotificationById(id)
    }

    suspend fun clearAllHistory() {
        notificationHistoryDao.clearAllHistory()
    }

    suspend fun updateNotification(notification: NotificationHistory) {
        notificationHistoryDao.update(notification)
    }
}