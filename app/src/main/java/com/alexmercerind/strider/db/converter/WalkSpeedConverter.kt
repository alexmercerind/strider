package com.alexmercerind.strider.db.converter

import androidx.room.TypeConverter
import com.alexmercerind.strider.enums.WalkSpeed

class WalkSpeedConverter {
    @TypeConverter
    fun fromStringToWalkSpeed(value: String): WalkSpeed {
        return WalkSpeed.valueOf(value)
    }

    @TypeConverter
    fun fromWalkSpeedToString(value: WalkSpeed): String {
        return value.name
    }
}
