package pl.lab2.subtrack.data.local

import androidx.room.TypeConverter
import pl.lab2.subtrack.data.local.entities.SubscriptionStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: SubscriptionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status: String): SubscriptionStatus {
        return SubscriptionStatus.valueOf(status)
    }
}
