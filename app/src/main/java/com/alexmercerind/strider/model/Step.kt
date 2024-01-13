package com.alexmercerind.strider.model

import androidx.room.Entity
import androidx.room.Index
import com.alexmercerind.strider.enums.WalkSpeed
import java.io.Serializable
import java.time.Instant

@Entity(
    tableName = "Step",
    primaryKeys = ["instant"],
    indices = [
        Index(
            "instant",
            "instant",
            orders = [Index.Order.ASC, Index.Order.DESC],
            name = "instant",
            unique = true
        )
    ]
)
data class Step(
    val instant: Instant,
    val MET: Float,
    val height: Float, /* cm */
    val weight: Float, /* kg */
    val speed: Float, /* m/s */
    val walkSpeed: WalkSpeed
) : Serializable
