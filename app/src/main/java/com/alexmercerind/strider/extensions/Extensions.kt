package com.alexmercerind.strider.extensions

import android.content.Context
import android.util.Log
import com.alexmercerind.strider.R
import com.alexmercerind.strider.model.Step
import com.alexmercerind.strider.util.Constants
import kotlin.math.roundToLong
import kotlin.math.sqrt

fun List<Step>.distance() = fold(0.0F) { result, it ->
    result + it.height * sqrt(2.0F) / 100_000L
}

fun Float.toDistanceString(context: Context) =
    if (this < 1.0F) "${(this * 1000.0F).roundToLong()} ${context.getString(R.string.unit_metre_small)}"
    else "%.2f ${context.getString(R.string.unit_kilometre_small)}".format(this)

fun List<Step>.calories() = fold(0.0F) { result, it ->
    val stride = it.height * sqrt(2.0F) / 100L
    val time = stride / it.speed
    val calories = time * it.MET * 3.5F * it.weight / (200L * 60L)
    result + calories
}

fun Float.toCaloriesString(context: Context) = "%.2f ${context.getString(R.string.unit_calories_small)}".format(this)
