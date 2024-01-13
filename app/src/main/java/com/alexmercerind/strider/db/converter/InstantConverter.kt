package com.alexmercerind.strider.db.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun fromLongToInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun fromInstantToLong(value: Instant): Long {
        return value.toEpochMilli()
    }
}
