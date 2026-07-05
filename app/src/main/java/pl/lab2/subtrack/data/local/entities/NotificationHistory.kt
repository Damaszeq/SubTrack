package pl.lab2.subtrack.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_history")
data class NotificationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subscriptionId: Long?,
    val serviceName: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)