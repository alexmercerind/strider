package com.alexmercerind.strider.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alexmercerind.strider.db.converter.InstantConverter
import com.alexmercerind.strider.db.converter.WalkSpeedConverter
import com.alexmercerind.strider.model.Step

@Database(entities = [Step::class], version = 1)
@TypeConverters(InstantConverter::class, WalkSpeedConverter::class)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao

    companion object {
        @Volatile
        private var instance: StepDatabase? = null

        private val lock = Any()

        operator fun invoke(application: Application) = instance ?: synchronized(lock) {
            instance ?: createDatabase(application).also { instance = it }
        }

        private fun createDatabase(application: Application) =
            Room.databaseBuilder(
                application,
                StepDatabase::class.java,
                "database.db"
            ).build()
    }
}
