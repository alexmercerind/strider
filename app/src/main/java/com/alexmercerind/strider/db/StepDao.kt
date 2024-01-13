package com.alexmercerind.strider.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexmercerind.strider.model.Step
import java.time.Instant

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: Step)

    @Query("SELECT * FROM Step WHERE instant > :from AND instant <= :to")
    fun getStepsInRange(from: Instant, to: Instant): List<Step>

    @Query("SELECT * FROM Step WHERE instant > :from AND instant <= :to")
    fun watchStepsInRange(from: Instant, to: Instant): LiveData<List<Step>>

    @Query("SELECT COUNT(*) FROM Step WHERE instant > :from AND instant <= :to")
    fun getStepCountInRange(from: Instant, to: Instant): Long

    @Query("SELECT COUNT(*) FROM Step WHERE instant > :from AND instant <= :to")
    fun watchStepCountInRange(from: Instant, to: Instant): LiveData<Long>
}
